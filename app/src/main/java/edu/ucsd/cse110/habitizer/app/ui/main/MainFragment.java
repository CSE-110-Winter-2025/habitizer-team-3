package edu.ucsd.cse110.habitizer.app.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentMainBinding;
import edu.ucsd.cse110.habitizer.app.TimerViewModel;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.AddTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.DeleteTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.EditRoutineDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.EditTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.state.AppState;
import edu.ucsd.cse110.habitizer.lib.domain.DeleteTaskDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineDialogParams;
import edu.ucsd.cse110.habitizer.app.ui.main.state.RoutineState;
import edu.ucsd.cse110.habitizer.app.ui.main.state.TimerState;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineBuilder;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.app.ui.main.updaters.UIRoutineUpdater;
import edu.ucsd.cse110.habitizer.app.ui.main.updaters.UITimerUpdater;
import edu.ucsd.cse110.habitizer.app.ui.main.updaters.UITaskUpdater;

public class MainFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentMainBinding view;
    private RecyclerView recyclerView;
    private TaskRecyclerViewAdapter adapter;
    private TimerViewModel timerViewModel;
    private TaskItemListener taskItemListener;
    private UIRoutineUpdater uiRoutineUpdater;
    private UITimerUpdater uiTimerUpdater;
    private UITaskUpdater uiTaskUpdater;
    private Routine currentRoutine;
    private AppSubject appSubject;

    ItemTouchHelper itemTouchHelper;


    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            Log.wtf("#", "onMove");
            adapter.exchangeOrder(from, to);
            adapter.notifyItemMoved(from, to);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
    }

    public MainFragment() {
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
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

        currentRoutine = activityModel.getCurrentRoutine();
        appSubject = new AppSubject(RoutineState.BEFORE, TimerState.REAL);

        uiRoutineUpdater = new UIRoutineUpdater();
        uiTaskUpdater = new UITaskUpdater();
        uiTimerUpdater = new UITimerUpdater();

        appSubject.observe(uiRoutineUpdater);
        appSubject.observe(uiTaskUpdater);
        appSubject.observe(uiTimerUpdater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the View
        this.view = FragmentMainBinding.inflate(inflater, container, false);
        recyclerView = view.taskView;
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter = new TaskRecyclerViewAdapter(currentRoutine.taskList(), taskItemListener, uiTaskUpdater);
        recyclerView.setAdapter(adapter);

        taskItemListener = new TaskItemListener() {
            @Override
            public void onEditClicked(Task task) {
                var activeRoutineId = currentRoutine.id();
                var taskId = task.id();

                assert activeRoutineId != null && taskId != null;
                var params = new EditTaskDialogParams(activeRoutineId, taskId, task.sortOrder(), task.taskTime());
                openEditTaskDialog(params);
            }

            @Override
            public void onDeleteClicked(Task task) {
                var activeRoutineId = currentRoutine.id();
                var taskId = task.id();

                assert activeRoutineId != null && taskId != null;
                var params = new DeleteTaskDialogParams(activeRoutineId, taskId, task.sortOrder());
                openDeleteTaskDialog(params);
            }

            @Override
            public void onCheckOffClicked(Task task) {
                task.setCheckedOff(true);
                int currentElapsed = timerViewModel.getElapsedSeconds().getValue() != null ?
                        timerViewModel.getElapsedSeconds().getValue() : 0;
                int taskTime = (int) Math.ceil((double) (currentElapsed - currentRoutine.taskList().lastTaskCheckoffTime()) / 60);
                task.setTaskTime(Math.max(taskTime, 1)); // make sure minimum is 1 second

                currentRoutine.taskList().setLastCheckoffTime(currentElapsed);

                if (currentRoutine.taskList().currentTaskId() < task.sortOrder()) {
                    currentRoutine.taskList().setCurrentTaskId(task.sortOrder() + 1);
                }
                recyclerView.setAdapter(adapter);
                recyclerView.invalidate();

                Log.d("TaskID", currentRoutine.taskList().currentTaskId().toString());
            }

            @Override
            public void onAllTaskCheckedOff() {
                endRoutine();
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateButtonVisibilities();

        timerViewModel.getElapsedSeconds().observe(getViewLifecycleOwner(), seconds -> {
            // Update a TextView to show elapsed minutes
            int minutes = seconds / 60;
            view.timerText.setText(String.valueOf(minutes));
        });

        view.routineEditButton.setOnClickListener(v -> {
            var activeRoutineId = currentRoutine.id();

            assert activeRoutineId != null;
            var params = new EditRoutineDialogParams(activeRoutineId);
            openEditRoutineDialog(params);
        });

        view.startButton.setOnClickListener(v -> {
            startRoutine();
        });

        view.stopButton.setOnClickListener(v -> {
            timerViewModel.stopTimer();
            appSubject.updateTimerState(TimerState.MOCK);
            updatePauseResumeButton();
            updateButtonVisibilities();
        });

        view.endButton.setOnClickListener(v -> {
            endRoutine();
        });

        view.createRoutineButton.setOnClickListener(v -> {
            Routine newRoutine = new RoutineBuilder().build();

            activityModel.updateRoutine(newRoutine);

            updateRoutineDropdown();

            // On create template routine, spinner should switch to that new routine
            List<Routine> routines = activityModel.getAllRoutines().getValue();
            if (routines != null && !routines.isEmpty()) {
                int newIndex = routines.size() - 1;
                activityModel.setCurrentRoutineId(newIndex);
                view.routineSpinner.setSelection(newIndex);
                updateCurrentRoutine();
            }
            updateButtonVisibilities();
        });


        view.fastforwardButton.setOnClickListener(v -> timerViewModel.forwardTimer());

        // Open the add task dialog upon clicking the add task button
        view.addTaskButton.setOnClickListener(w -> {
            openAddTaskDialog();
        });

        view.pauseResumeButton.setOnClickListener(v -> {
           updatePauseResumeButton();
        });

        view.time.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // The user finished editing "time" vvv
                String newTimeStr = view.time.getText().toString();
                if (!newTimeStr.isEmpty()) {
                    int newTime = Integer.parseInt(newTimeStr);

                    var updateRoutine = currentRoutine.withTime(newTime);

                    activityModel.updateRoutine(updateRoutine);
                }
            }
        });

        return view.getRoot();
    }

    private void startRoutine() {
        timerViewModel.startTimer();
        appSubject.updateRoutineState(RoutineState.DURING);
        updateButtonVisibilities();
        adapter.notifyDataSetChanged();
    }
    private void endRoutine() {
        timerViewModel.stopTimer();
        appSubject.updateRoutineState(RoutineState.AFTER);
        updateButtonVisibilities();
        view.startButton.setText("Routine Ended"); // We should really stop doing this...
        view.startButton.setEnabled(false);
        recyclerView.post(() -> {
            adapter.notifyDataSetChanged();
        });
    }

    private void updateButtonVisibilities() {
        view.startButton.setVisibility(uiRoutineUpdater.showStart() ? View.VISIBLE : View.GONE);
        view.startButton.setEnabled(!currentRoutine.taskList().isEmpty());
        view.stopButton.setVisibility((uiRoutineUpdater.showStop() && uiTimerUpdater.showStop()) ? View.VISIBLE : View.GONE);
        view.endButton.setVisibility((uiRoutineUpdater.showEnd() && uiTimerUpdater.showEnd()) ? View.VISIBLE : View.GONE);
        view.fastforwardButton.setVisibility((uiRoutineUpdater.showFastForward() && uiTimerUpdater.showFastForward()) ? View.VISIBLE : View.GONE);
        view.pauseResumeButton.setVisibility((uiRoutineUpdater.showPause() && uiTimerUpdater.showPauseResume()) ? View.VISIBLE : View.GONE);
        view.addTaskButton.setVisibility(uiRoutineUpdater.showAdd() ? View.VISIBLE : View.GONE);
        view.time.setEnabled(uiRoutineUpdater.canEditRoutine());
        view.createRoutineButton.setVisibility(uiRoutineUpdater.showCreateRoutine() ? View.VISIBLE : View.GONE);
        view.routineEditButton.setVisibility(uiRoutineUpdater.canEditRoutine() ? View.VISIBLE : View.GONE);
        view.routineSpinner.setEnabled(uiRoutineUpdater.canEditRoutine());
    }

    public void onViewCreated(@NonNull View view2, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view2, savedInstanceState);

        activityModel.getAllRoutines().observe(routines -> {
            if (routines == null) return;
            updateRoutineDropdown();
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // If you want to stop the timer when the Fragment is destroyed
        // timerViewModel.stopTimer();
    }


    private void openEditTaskDialog(EditTaskDialogParams params) {
        var dialogFragment = EditTaskDialogFragment.newInstance(params);
        dialogFragment.show(getParentFragmentManager(), "EditTaskDialogFragment");
    }

    private void openDeleteTaskDialog(DeleteTaskDialogParams params) {
        var dialogFragment = DeleteTaskDialogFragment.newInstance(params);
        dialogFragment.show(getParentFragmentManager(), "DeleteTaskDialogFragment");
    }

    private void openAddTaskDialog() {
        var dialogFragment = AddTaskDialogFragment.newInstance();
        dialogFragment.show(getParentFragmentManager(), "AddTaskDialogFragment");

        // listen for when the dialog is closed
        getParentFragmentManager().setFragmentResultListener("ADD_TASK_DIALOG_DISMISSED", this, (requestKey, result) -> {
            if (result.getBoolean("dialog_dismissed", false)) {
                recyclerView.post(() -> updateButtonVisibilities()); // Ensure UI updates after task addition
            }
        });
    }

    private void openEditRoutineDialog(EditRoutineDialogParams params) {
        var dialogFragment = EditRoutineDialogFragment.newInstance(params);
        dialogFragment.show(getParentFragmentManager(), "EditRoutineDialogFragment");
    }

    private void updateRoutineDropdown() {
        List<Routine> routines = activityModel.getAllRoutines().getValue();
        if (routines == null || routines.isEmpty()) return;

        List<String> routineNames = new ArrayList<>();
        for (Routine routine : routines) {
            routineNames.add(routine.name());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, routineNames);
        view.routineSpinner.setAdapter(spinnerAdapter);

        // Find the index of the current routine safely
        int currentRoutineIndex = 0;
        for (int i = 0; i < routines.size(); i++) {
            if (Objects.equals(routines.get(i).id(), activityModel.getCurrentRoutine().id())) {
                currentRoutineIndex = i;
                break;
            }
        }
        view.routineSpinner.setSelection(currentRoutineIndex);

        view.routineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View spinnerView, int position, long id) {
                Routine selectedRoutine = routines.get(position);
                activityModel.setCurrentRoutineId(selectedRoutine.id());
                updateCurrentRoutine();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    private void updateCurrentRoutine() {
        currentRoutine = activityModel.getCurrentRoutine();
        view.time.setText(currentRoutine.time() != null ? String.valueOf(currentRoutine.time()) : "");
        adapter = new TaskRecyclerViewAdapter(currentRoutine.taskList(), taskItemListener, uiTaskUpdater);
        recyclerView.setAdapter(adapter);
    }

    private void updatePauseResumeButton() {
        if (timerViewModel.isPaused()) {
            timerViewModel.resumeTimer();
            view.pauseResumeButton.setImageResource(R.drawable.baseline_pause_24);
            view.fastforwardButton.setEnabled(true);
        } else {
            timerViewModel.pauseTimer();
            view.pauseResumeButton.setImageResource(R.drawable.baseline_play_arrow_24);
            view.fastforwardButton.setEnabled(false);
        }
    }
}
