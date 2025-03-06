package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskList;
import edu.ucsd.cse110.habitizer.lib.util.MutableSubject;
import edu.ucsd.cse110.habitizer.lib.util.SimpleSubject;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;
    private final MutableSubject<List<Routine>> allRoutines;
    private Integer currentRoutineId = 0;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;

        // Create the observable subjects.
        this.allRoutines = new SimpleSubject<>();

        // Observe all routines from the repo
        routineRepository.findAll().observe(routines -> {
            if (routines == null) return;
            allRoutines.setValue(routines);
        });
    }

    // Getters so the Activity/Fragment can observe or retrieve the Subjects
    public Subject<List<Routine>> getAllRoutines() {
        return allRoutines;
    }

    public void addTaskToRoutine(Integer routineId, Task task) {
        routineRepository.addTaskToRoutine(routineId, task);
    }

    public void editTask(EditTaskRequest req) {
        routineRepository.editTask(req);
    }

    public void updateRoutine(Routine routine) {
        routineRepository.save(routine);
    }

    public Routine getCurrentRoutine() {
        List<Routine> routines = allRoutines.getValue();
        if (routines == null || routines.isEmpty()) {
            return null; // Return null if no routines are available yet
        }

        // Ensure currentRoutineId is valid
        if (currentRoutineId >= routines.size()) {
            currentRoutineId = 0; // Reset to first routine if the ID is out of bounds
        }

        // Get the routine
        Routine routine = routines.get(currentRoutineId);

        // Validate the routine has a taskList
        if (routine != null && routine.taskList() == null) {
            // This shouldn't happen if RoomRoutineRepository is properly loading tasks,
            // but just in case, create an empty TaskList
            return routine.withTasks(new TaskList(new ArrayList<>()));
        }

        return routine;
    }

    public void setCurrentRoutineId(Integer id) { currentRoutineId = id; }
    public Integer getCurrentRoutineId() { return currentRoutineId; }
}