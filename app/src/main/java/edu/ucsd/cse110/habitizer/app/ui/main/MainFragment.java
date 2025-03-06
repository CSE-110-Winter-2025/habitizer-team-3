package edu.ucsd.cse110.habitizer.app.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentMainBinding;
import edu.ucsd.cse110.habitizer.app.TimerViewModel;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.AddTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.dialogs.EditTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.main.state.AppState;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskDialogParams;
import edu.ucsd.cse110.habitizer.app.ui.main.state.RoutineState;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.app.ui.main.updaters.UIRoutineUpdater;
import edu.ucsd.cse110.habitizer.app.ui.main.updaters.UITaskUpdater;
import edu.ucsd.cse110.habitizer.lib.util.Observer;

import java.util.List;

public class MainFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentMainBinding view;
    private RecyclerView recyclerView;
    private TaskRecyclerViewAdapter adapter;
    private TimerViewModel timerViewModel;
    private TaskItemListener taskItemListener;
    private UIRoutineUpdater uiRoutineUpdater;
    private UITaskUpdater uiTaskUpdater;
    private Routine currentRoutine;
    private AppState state;
    private boolean initialDataLoaded = false;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateSwappedRoutine() {
        currentRoutine = activityModel.getCurrentRoutine();
        if (currentRoutine == null) {
            return;
        }

        logCurrentRoutine();

        view.routineName.setText(currentRoutine.name());
        view.time.setText(String.valueOf(currentRoutine.time()));

        // Only create adapter if taskList exists
        if (currentRoutine.taskList() != null) {
            adapter = new TaskRecyclerViewAdapter(currentRoutine.taskList(), taskItemListener, uiTaskUpdater);
            recyclerView.setAdapter(adapter);
        }
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

        // Initialize state before we have the current routine
        state = new AppState();
        state.setValue(RoutineState.BEFORE);

        uiRoutineUpdater = new UIRoutineUpdater();
        uiTaskUpdater = new UITaskUpdater();
        state.observe(uiRoutineUpdater);
        state.observe(uiTaskUpdater);

        // Setup task item listener early
        taskItemListener = createTaskItemListener();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the View
        this.view = FragmentMainBinding.inflate(inflater, container, false);
        recyclerView = view.taskView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize UI components that don't depend on currentRoutine
        setupButtonClickListeners();
        setupTimerObserver();

        // We'll set up the adapter once data is loaded in onViewCreated

        return view.getRoot();
    }

    private void setupButtonClickListeners() {
        view.startButton.setOnClickListener(v -> startRoutine());
        view.stopButton.setOnClickListener(v -> timerViewModel.stopTimer());
        view.endButton.setOnClickListener(v -> endRoutine());
        view.fastforwardButton.setOnClickListener(v -> timerViewModel.forwardTimer());
        view.addTaskButton.setOnClickListener(w -> openAddTaskDialog());
        view.swapButton.setOnClickListener(v -> {
            // TODO: update this logic when we have multiple routines
            Integer currentRoutineId = activityModel.getCurrentRoutineId();
            if (currentRoutineId == 0) activityModel.setCurrentRoutineId(1);
            else activityModel.setCurrentRoutineId(0);
            updateSwappedRoutine();
        });

        view.time.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && currentRoutine != null) {
                // The user finished editing "time"
                String newTimeStr = view.time.getText().toString();
                if (!newTimeStr.isEmpty()) {
                    int newTime = Integer.parseInt(newTimeStr);
                    var updateRoutine = currentRoutine.withTime(newTime);
                    activityModel.updateRoutine(updateRoutine);
                }
            }
        });
    }

    private void setupTimerObserver() {
        timerViewModel.getElapsedSeconds().observe(getViewLifecycleOwner(), seconds -> {
            // Update a TextView to show elapsed minutes
            int minutes = seconds / 60;
            view.timerText.setText(String.valueOf(minutes));
        });
    }

    private TaskItemListener createTaskItemListener() {
        return new TaskItemListener() {
            @Override
            public void onEditClicked(Task task) {
                if (currentRoutine == null) return;

                var activeRoutineId = currentRoutine.id();
                var taskId = task.id();

                if (activeRoutineId == null || taskId == null) return;

                var params = new EditTaskDialogParams(activeRoutineId, taskId, task.sortOrder(), task.taskTime());
                openEditTaskDialog(params);
            }

            @Override
            public void onCheckOffClicked(Task task) {
                if (currentRoutine == null) return;

                task.setCheckedOff(true);
                int currentElapsed = timerViewModel.getElapsedSeconds().getValue() != null ?
                        timerViewModel.getElapsedSeconds().getValue() : 0;
                int taskTime = (int) Math.ceil((double) (currentElapsed - currentRoutine.taskList().lastTaskCheckoffTime()) / 60);
                task.setTaskTime(Math.max(taskTime, 1)); // make sure minimum is 1 second

                currentRoutine.taskList().setLastCheckoffTime(currentElapsed);

                if (currentRoutine.taskList().currentTaskId() < task.sortOrder()) {
                    currentRoutine.taskList().setCurrentTaskId(task.sortOrder() + 1);
                }
                if (adapter != null) {
                    recyclerView.setAdapter(adapter);
                    recyclerView.invalidate();
                }

                Log.d("TaskID", currentRoutine.taskList().currentTaskId().toString());
            }

            @Override
            public void onAllTaskCheckedOff() {
                endRoutine();
            }
        };
    }

    public void onViewCreated(@NonNull View view2, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view2, savedInstanceState);

        activityModel.getAllRoutines().observe(routines -> {
            if (routines == null || routines.isEmpty()) {
                Log.d("MainFragment", "No routines available");
                return;
            }

            Log.d("MainFragment", "Received " + routines.size() + " routines");
            for (Routine r : routines) {
                debugRoutineData(r);
            }

            // At this point we know we have routines data
            initialDataLoaded = true;
            currentRoutine = activityModel.getCurrentRoutine();
            debugRoutineData(currentRoutine); // Debug the current routine

            // Only proceed if we have a valid routine with a task list
            if (currentRoutine != null && currentRoutine.taskList() != null) {
                // Initialize the adapter now that we have data
                adapter = new TaskRecyclerViewAdapter(currentRoutine.taskList(), taskItemListener, uiTaskUpdater);
                recyclerView.setAdapter(adapter);

                // Update UI with routine data
                updateSwappedRoutine();
                updateButtonVisibilities();
            }
        });
    }
    private void logCurrentRoutine() {
        if (currentRoutine == null) {
            Log.d("MainFragment", "Current routine is null");
            return;
        }

        Log.d("MainFragment", "Current routine: " + currentRoutine.id() + " - " + currentRoutine.name());

        if (currentRoutine.taskList() == null) {
            Log.d("MainFragment", "Task list is null");
            return;
        }

        if (currentRoutine.taskList().tasks() == null) {
            Log.d("MainFragment", "Tasks collection is null");
            return;
        }

        Log.d("MainFragment", "Tasks count: " + currentRoutine.taskList().tasks().size());
    }

    private void debugRoutineData(Routine routine) {
        if (routine == null) {
            Log.d("MainFragment", "Routine is null");
            return;
        }

        Log.d("MainFragment", "Routine: " + routine.id() + " - " + routine.name() + " (" + routine.time() + " min)");

        if (routine.taskList() == null) {
            Log.d("MainFragment", "  TaskList is null");
            return;
        }

        if (routine.taskList().tasks() == null) {
            Log.d("MainFragment", "  Tasks collection is null");
            return;
        }

        Log.d("MainFragment", "  Task count: " + routine.taskList().tasks().size());

        for (Task task : routine.taskList().tasks()) {
            Log.d("MainFragment", "    Task: " + task.id() + " - " + task.name() +
                    " (Sort: " + task.sortOrder() + ", Checked: " + task.isCheckedOff() + ")");
        }
    }

    private void startRoutine() {
        if (currentRoutine == null) return;

        timerViewModel.startTimer();
        state.setValue(RoutineState.DURING);
        updateButtonVisibilities();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void endRoutine() {
        if (currentRoutine == null) return;

        timerViewModel.stopTimer();
        state.setValue(RoutineState.AFTER);
        updateButtonVisibilities();
        view.startButton.setText("Routine Ended"); // We should really stop doing this...
        view.startButton.setEnabled(false);
        if (adapter != null) {
            recyclerView.post(() -> {
                adapter.notifyDataSetChanged();
            });
        }
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

    private void openAddTaskDialog() {
        var dialogFragment = AddTaskDialogFragment.newInstance();
        dialogFragment.show(getParentFragmentManager(), "AddTaskDialogFragment");
    }
}