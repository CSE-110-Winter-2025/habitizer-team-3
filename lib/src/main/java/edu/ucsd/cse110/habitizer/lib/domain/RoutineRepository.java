package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        // Add the new task to the task list
        var numTasks = routine.tasks().size();
        var newTask = task.withIdAndSortOrder(numTasks, numTasks);
        var taskList = routine.tasks();
        var newTaskList = Stream.concat(taskList.stream(), Stream.of(newTask)).collect(Collectors.toList());

        // Create a new routine object to save
        Routine newRoutine = new Routine(routineId, routine.name(), newTaskList);

        dataSource.putRoutine(newRoutine);
    }
}
