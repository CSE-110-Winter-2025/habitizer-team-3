package edu.ucsd.cse110.habitizer.app.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentMainBinding;
import edu.ucsd.cse110.habitizer.app.TimerViewModel;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.AddTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.EditTaskDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.util.UIRoutineUpdater;
import edu.ucsd.cse110.habitizer.lib.util.UITaskUpdater;

public class MainFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentMainBinding view;
//    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private TaskRecyclerViewAdapter adapter;
    private Routine currentRoutine;
    private TimerViewModel timerViewModel;
    private TaskItemListener taskItemListener;
    private UIRoutineUpdater uiRoutineUpdater;
    private UITaskUpdater uiTaskUpdater;
    private boolean showMorningRoutine = true;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private void updateSwappedRoutine() {
        Routine activeRoutine = getCurrentRoutine();

        view.routineName.setText(activeRoutine.name());
        view.time.setText(String.valueOf(activeRoutine.time()));

        Integer routineId = activeRoutine.id();
        if (routineId == null) {
            routineId = 0;
        }

        adapter = new TaskRecyclerViewAdapter(activeRoutine.taskList(), taskItemListener, uiTaskUpdater);
        recyclerView.setAdapter(adapter);
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

        currentRoutine = getCurrentRoutine();

        uiRoutineUpdater = new UIRoutineUpdater();
        uiTaskUpdater = new UITaskUpdater();
        currentRoutine.observe(uiRoutineUpdater);
        currentRoutine.observe(uiTaskUpdater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the View
        this.view = FragmentMainBinding.inflate(inflater, container, false);

        recyclerView = view.taskView;
        adapter = new TaskRecyclerViewAdapter(currentRoutine.taskList(), taskItemListener, uiTaskUpdater);
        recyclerView.setAdapter(adapter);

        taskItemListener = new TaskItemListener() {
            @Override
            public void onEditClicked(Task task) {
                var activeRoutineId = getCurrentRoutine().id();
                var taskId = task.id();

                assert activeRoutineId != null && taskId != null;
                var params = new EditTaskDialogParams(activeRoutineId, taskId, task.sortOrder(), task.taskTime());
                openEditTaskDialog(params);
            }

            @Override
            public void onCheckOffClicked(Task task) {
                task.setCheckedOff(true);
                int currentElapsed = timerViewModel.getElapsedSeconds().getValue() != null ?
                        timerViewModel.getElapsedSeconds().getValue() : 0;
                int taskTime = (int) Math.ceil((double) (currentElapsed - currentRoutine.taskList().lastTaskCheckoffTime()) / 60);
                task.setTaskTime(taskTime);

                currentRoutine.taskList().setLastCheckoffTime(currentElapsed);

                if (currentRoutine.taskList().currentTaskId() < task.sortOrder()) {
                    currentRoutine.taskList().setCurrentTaskId(task.sortOrder() + 1);

                    recyclerView.post(() -> {
                        adapter.notifyItemRangeChanged(0, currentRoutine.taskList().currentTaskId() + 1);
                    });
                } else {
                    recyclerView.post(() -> {
                        adapter.notifyItemChanged(task.sortOrder());
                    });
                }

                Log.d("TaskID", currentRoutine.taskList().currentTaskId().toString());
            }

            @Override
            public void onAllTaskCheckedOff() {
                endRoutine();
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d("MainFragment", "Routine State: " + currentRoutine.getValue());
        updateButtonVisibilities();

        timerViewModel.getElapsedSeconds().observe(getViewLifecycleOwner(), seconds -> {
            // Update a TextView to show elapsed minutes
            int minutes = seconds / 60;
            view.timerText.setText(String.valueOf(minutes));
        });

        view.startButton.setOnClickListener(v -> {
            startRoutine();
        });

        view.stopButton.setOnClickListener(v -> {
            timerViewModel.stopTimer();
        });

        view.endButton.setOnClickListener(v -> {
            endRoutine();
        });

        view.fastforwardButton.setOnClickListener(v -> timerViewModel.forwardTimer());

        // Open the add task dialog upon clicking the add task button
        view.addTaskButton.setOnClickListener(w -> {
            openAddTaskDialog();
        });

        view.swapButton.setOnClickListener(v -> {
            showMorningRoutine = !showMorningRoutine;
            updateSwappedRoutine();
        });

        view.time.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // The user finished editing "time" vvv
                String newTimeStr = view.time.getText().toString();
                if (!newTimeStr.isEmpty()) {
                    int newTime = Integer.parseInt(newTimeStr);
                    var routines = activityModel.getAllRoutines().getValue();
                    if (routines == null) return;
                    var routine = getCurrentRoutine();

                    var updateRoutine = routine.withTime(newTime);

                    activityModel.updateRoutine(updateRoutine);
                    currentRoutine = getCurrentRoutine();
                }
            }
        });

        return view.getRoot();
    }

    private void startRoutine() {
        timerViewModel.startTimer();
        currentRoutine.startRoutine();
        updateButtonVisibilities();
        adapter.notifyDataSetChanged();
    }
    private void endRoutine() {
        timerViewModel.stopTimer();
        currentRoutine.endRoutine();
        updateButtonVisibilities();
        view.startButton.setText("Routine Ended"); // We should really stop doing this...
        view.startButton.setEnabled(false);
        recyclerView.post(() -> {
            adapter.notifyDataSetChanged();
        });
    }

    private void updateButtonVisibilities() {
        view.startButton.setVisibility(uiRoutineUpdater.showStart() ? View.VISIBLE : View.GONE);
        view.stopButton.setVisibility(uiRoutineUpdater.showStop() ? View.VISIBLE : View.GONE);
        view.endButton.setVisibility(uiRoutineUpdater.showEnd() ? View.VISIBLE : View.GONE);
        view.fastforwardButton.setVisibility(uiRoutineUpdater.showFastForward() ? View.VISIBLE : View.GONE);
        view.addTaskButton.setVisibility(uiRoutineUpdater.showAdd() ? View.VISIBLE : View.GONE);
        view.swapButton.setEnabled(uiRoutineUpdater.canSwap());
        view.time.setEnabled(uiRoutineUpdater.canEditTime());

    }

    public void onViewCreated(@NonNull View view2, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view2, savedInstanceState);

        activityModel.getAllRoutines().observe(routines -> {
            if (routines == null) return;
            updateSwappedRoutine();
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // If you want to stop the timer when the Fragment is destroyed
        // timerViewModel.stopTimer();
    }

    private Routine getCurrentRoutine() {
        List<Routine> routines = activityModel.getAllRoutines().getValue();
        assert routines != null && routines.size() >= 2;

        return showMorningRoutine ? routines.get(0) : routines.get(1);
    }

    private void openEditTaskDialog(EditTaskDialogParams params) {
        var dialogFragment = EditTaskDialogFragment.newInstance(params);
        dialogFragment.show(getParentFragmentManager(), "EditTaskDialogFragment");
    }

    private void openAddTaskDialog() {
        var dialogFragment = AddTaskDialogFragment.newInstance();
        dialogFragment.show(getParentFragmentManager(), "AddTaskDialogFragment");
    }
}
