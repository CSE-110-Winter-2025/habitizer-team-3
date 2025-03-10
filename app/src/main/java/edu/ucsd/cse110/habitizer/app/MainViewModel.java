package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.DeleteTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineRequest;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;
    private final Subject<List<Routine>> allRoutines;
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
        this.allRoutines = new Subject<>();

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
    public void deleteTask(DeleteTaskRequest req) { routineRepository.deleteTask(req); }

    public void editRoutine(EditRoutineRequest req) { routineRepository.editRoutineName(req); }

    public void updateRoutine(Routine routine) {
        routineRepository.save(routine);
    }
    public Routine getCurrentRoutine() {
        List<Routine> routines = allRoutines.getValue();
        assert routines != null && routines.size() >= 2;

        return routines.get(currentRoutineId);
    }

    public void setCurrentRoutineId(Integer id) { currentRoutineId = id; }
    public Integer getCurrentRoutineId() { return currentRoutineId; }
}
