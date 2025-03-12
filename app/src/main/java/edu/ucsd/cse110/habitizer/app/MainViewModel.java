package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.domain.AppRepository;
import edu.ucsd.cse110.habitizer.lib.domain.DeleteTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineRequest;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.App;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineState;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;
import edu.ucsd.cse110.habitizer.lib.util.MutableSubject;
import edu.ucsd.cse110.habitizer.lib.util.SimpleSubject;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;
    private final AppRepository appRepository;
    private final MutableSubject<List<Routine>> allRoutines;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<Integer> currentRoutineId;
    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository(), app.getAppRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository, AppRepository appRepository) {
        this.routineRepository = routineRepository;
        this.appRepository = appRepository;
        this.allRoutines = new SimpleSubject<>();
        this.currentRoutine = new SimpleSubject<>();
        this.currentRoutineId = new SimpleSubject<>();

        // Auto-select first routine when data loads
        routineRepository.findAll().observe(routines -> {
            if (routines == null) return;
            allRoutines.setValue(routines);
        });

        this.currentRoutineId.setValue(0);

        this.currentRoutineId.observe(currId -> {
            if (currId == null) return;
            refreshCurrentRoutine();
        });


    }

    public Subject<List<Routine>> getAllRoutines() {
        return allRoutines;
    }

    public void addTaskToRoutine(Integer routineId, Task task) {
        routineRepository.addTaskToRoutine(routineId, task);
    }

    public void editTask(EditTaskRequest req) {
        routineRepository.editTask(req);
    }
    public void deleteTask(DeleteTaskRequest req) { routineRepository.deleteTask(req); }

    public void editRoutine(EditRoutineRequest req) { routineRepository.editRoutineName(req); }

    public void updateRoutine(Routine routine) {
        routineRepository.save(routine);
    }

    public Subject<Routine> getCurrentRoutine() {
        return currentRoutine;
    }

    public void setCurrentRoutineId(Integer id) {
        currentRoutineId.setValue(id);
    }
    public Integer getCurrentRoutineId() { return currentRoutineId.getValue(); }

    public void refreshCurrentRoutine() {
        Integer id = Objects.requireNonNull(currentRoutineId.getValue());
        Log.d("MainViewModel", String.valueOf(id));
        routineRepository.find(id).observe(routine -> {
            if (routine != null) currentRoutine.setValue(routine);
        });
    }

    public Integer getNumRoutines() { return allRoutines.getValue().size(); }

    public void updateTasks(List<Task> tasks) {
        for (var task : tasks) {
            var req = new EditTaskRequest(
                    Objects.requireNonNull(currentRoutineId.getValue()),
                    Objects.requireNonNull(task.id()),
                    task.sortOrder(),
                    task.name(),
                    task.taskTime()
            );
            routineRepository.editTask(req);
        }
    }

    public Subject<App> getAppState() {
        return appRepository.find();
    }

    public void updateRoutineState(RoutineState rs) {
        App currApp = appRepository.find().getValue();
        appRepository.save(new App(rs, currApp.timerState(), currApp.timerTime(), currApp.taskTime(), currApp.currentRoutineId()));
    }

    public void updateTimerState(TimerState ts) {
        App currApp = appRepository.find().getValue();
        appRepository.save(new App(currApp.routineState(), ts, currApp.timerTime(), currApp.taskTime(), currApp.currentRoutineId()));
    }

    public void updateTimerTime(Integer timerTime) {
        App currApp = appRepository.find().getValue();
        appRepository.save(new App(currApp.routineState(), currApp.timerState(), timerTime, currApp.taskTime(), currApp.currentRoutineId()));
    }

    public void updateTaskTime(Integer taskTime) {
        App currApp = appRepository.find().getValue();
        appRepository.save(new App(currApp.routineState(), currApp.timerState(), currApp.timerTime(), taskTime, currApp.currentRoutineId()));
    }

    public void updateCurrentRoutineId(Integer routineId) {
        App currApp = appRepository.find().getValue();
        appRepository.save(new App(currApp.routineState(), currApp.timerState(), currApp.timerTime(), currApp.taskTime(), routineId));
    }
}