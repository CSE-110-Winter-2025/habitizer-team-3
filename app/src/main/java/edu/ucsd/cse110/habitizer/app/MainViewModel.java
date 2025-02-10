package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;
    private final Subject<List<Routine>> allRoutines;

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
}
