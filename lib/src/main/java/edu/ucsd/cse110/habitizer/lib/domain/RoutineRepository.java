package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        Routine routine = Objects.requireNonNull(find(routineId).getValue());

        // Add the new task to the task list
        var numTasks = routine.tasks().size();

        // new task should have id and sortOrder of numTasks
        var newTask = task.withIdAndSortOrder(numTasks, numTasks);
        var taskList = routine.tasks();
        var newTaskList = Stream.concat(taskList.stream(), Stream.of(newTask)).collect(Collectors.toList());

        // Create a new routine object to save
        Routine newRoutine = routine.withTasks(newTaskList);

        dataSource.putRoutine(newRoutine);
    }

    public void editTask(EditTaskRequest req) {
        Routine routine = Objects.requireNonNull(find(req.routineId()).getValue());

        // Make a new task object with the new name
        var newTask = new Task(req.taskId(), req.taskName(), req.sortOrder(), req.taskTime());

        // Create a new list with the new task in the correct position
        var taskList = new ArrayList<>(List.copyOf(routine.tasks()));
        taskList.remove((int) req.sortOrder());
        taskList.add(req.sortOrder(), newTask);

        // Make a new routine with the updated task list
        Routine newRoutine = routine.withTasks(taskList);

        dataSource.putRoutine(newRoutine);
    }
}
