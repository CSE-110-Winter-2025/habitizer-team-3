package edu.ucsd.cse110.habitizer.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentMainBinding;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.habitizer.app.TimerViewModel;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.AddTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.EditTaskDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class MainFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentMainBinding view;
    private ListItemTaskBinding binding;
    private TaskAdapter adapter;
    private TimerViewModel timerViewModel;
    private List<Task> tasks;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timerViewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (empty for now)
        this.adapter = new TaskAdapter(requireContext(), List.of(), 0, editTaskDialogParams -> {
            var dialogFragment = EditTaskDialogFragment.newInstance(editTaskDialogParams);
            dialogFragment.show(getParentFragmentManager(), "EditTaskDialogFragment");
        });


        adapter.setTaskItemListener(new TaskItemListener() {
            @Override
            public void onTaskClicked(Task task) {
            }

            @Override
            public void onEditClicked(Task task) {
            }

            @Override
            public void onCheckOffClicked(Task task) {
                Task updatedTask = timerViewModel.checkOffTaskAndReturnUpdated(task);
                updatedTask.setCheckedOff(true);
                adapter.updateTask(task, updatedTask);
            }

            @Override
            public void onAllTaskCheckedOff() {
                endRoutine();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the View
        this.view = FragmentMainBinding.inflate(inflater, container, false);

        // Initially hide stop and fast-forward buttons
        view.stopButton.setVisibility(View.GONE);
        view.fastforwardButton.setVisibility(View.GONE);
        view.endButton.setVisibility(View.GONE);

        // Observe the timer's LiveData
        timerViewModel.getElapsedSeconds().observe(getViewLifecycleOwner(), seconds -> {
            // Update a TextView to show elapsed minutes
            int minutes = seconds / 60;
            view.timerText.setText(String.valueOf(minutes));
        });

        view.startButton.setOnClickListener(v -> {
            timerViewModel.startTimer();
            int currentElapsed = timerViewModel.getElapsedSeconds().getValue() != null ?
                    timerViewModel.getElapsedSeconds().getValue() : 0;
            timerViewModel.resetPrevTaskTime(currentElapsed);

            view.startButton.setVisibility(View.GONE);
            view.stopButton.setVisibility(View.VISIBLE);
            view.endButton.setVisibility(View.VISIBLE);
            view.fastforwardButton.setVisibility(View.VISIBLE);
            view.addTaskButton.setVisibility(View.GONE);

            adapter.onStartButtonPressed();
        });

        view.stopButton.setOnClickListener(v -> {
            timerViewModel.stopTimer();
        });

        view.endButton.setOnClickListener(v -> {
            endRoutine();
        });

        view.fastforwardButton.setOnClickListener(v -> timerViewModel.forwardTimer());

        view.taskList.setAdapter(adapter);



        // Listen for time field changes (focus lost => update the model)
        view.time.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // The user finished editing "time" vvv
                String newTimeStr = view.time.getText().toString();
                if (!newTimeStr.isEmpty()) {
                    int newTime = Integer.parseInt(newTimeStr);
                    var routines = activityModel.getAllRoutines().getValue();
                    if (routines == null) return;
                    var routine = routines.get(0);

                    var updateRoutine = routine.withTime(newTime);

                    activityModel.updateRoutine(updateRoutine);
                }
            }
        });


        // Open the add task dialog upon clicking the add task button
        view.addTaskButton.setOnClickListener(w -> {
            var dialogFragment = AddTaskDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "AddTaskDialogFragment");
        });

        return view.getRoot();
    }

    private void endRoutine() {
        timerViewModel.stopTimer();
        view.startButton.setVisibility(View.VISIBLE);
        view.startButton.setText(R.string.routine_ended);
        view.startButton.setEnabled(false);
        view.stopButton.setVisibility(View.GONE);
        view.endButton.setVisibility(View.GONE);
        view.fastforwardButton.setVisibility(View.GONE);
    }
    public void onViewCreated(@NonNull View view2, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view2, savedInstanceState);

        // 4) Observe routines AFTER the view is ready, so we can safely update UI
        activityModel.getAllRoutines().observe(routines -> {
            if (routines == null) return;

            // TODO: assign the routine id dynamically
            var morningRoutine = routines.get(0);

            // Show the routine name in a TextView
            view.routineName.setText(morningRoutine.name());
            view.time.setText(morningRoutine.time().toString());

            // If you have a time field on the Routine, display it in the EditText
            // e.g. binding.time.setText(String.valueOf(morningRoutine.time()));

            // Show tasks in the adapter
            var tasks = morningRoutine.tasks();

            adapter.clear();
            adapter.addAll(tasks);
            adapter.notifyDataSetChanged();
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // If you want to stop the timer when the Fragment is destroyed
        // timerViewModel.stopTimer();
    }
}
