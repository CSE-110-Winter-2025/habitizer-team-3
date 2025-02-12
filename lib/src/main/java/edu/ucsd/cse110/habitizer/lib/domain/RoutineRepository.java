package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class RoutineRepository {
    private final InMemoryDataSource dataSource;

    public RoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer count() {
        return dataSource.getRoutines().size();
    }

    public Subject<Routine> find(int id) {
        return dataSource.getRoutineSubject(id);
    }

    public Subject<List<Routine>> findAll() {
        return dataSource.getAllRoutinesSubject();
    }

    public void save(Routine routine) {
        dataSource.putRoutine(routine);
    }

    public void addTaskToRoutine(int routineId, @NonNull Task task) {
        Routine routine = find(routineId).getValue();

        if (routine == null) {
            throw new IllegalArgumentException("Routine with ID " + routineId + " not found.");
        }

        var numTasks = routine.tasks().size();
        var newTask = task.withIdAndSortOrder(numTasks, numTasks);
        routine.addTask(newTask);

        var newRoutine = new Routine(routineId, routine.name(), routine.tasks());

        dataSource.putRoutine(newRoutine);
    }
}
