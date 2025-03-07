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
        // If the routine doesn't have an id, assign one
        if (routine.id() == null) {
            // assign one greater than the current max id
            int newId = dataSource.getRoutines().stream()
                    .map(r -> r.id() == null ? 0 : r.id())
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
            routine = routine.withId(newId);
        }
        dataSource.putRoutine(routine);
    }

    public void addTaskToRoutine(int routineId, @NonNull Task task) {
        Routine routine = Objects.requireNonNull(find(routineId).getValue());

        var numTasks = routine.taskList().tasks().size();

        var newTask = task.withIdAndSortOrder(numTasks, numTasks);
        var taskList = routine.taskList().tasks();
        var newTaskList = Stream.concat(taskList.stream(), Stream.of(newTask)).collect(Collectors.toList());

        Routine newRoutine = routine.withTasks(new TaskList(newTaskList));

        dataSource.putRoutine(newRoutine);
    }

    public void editTask(EditTaskRequest req) {
        Routine routine = Objects.requireNonNull(find(req.routineId()).getValue());

        var newTask = new Task(req.taskId(), req.taskName(), req.sortOrder(), req.taskTime());

        var taskList = new ArrayList<>(List.copyOf(routine.taskList().tasks()));
        taskList.remove((int) req.sortOrder());
        taskList.add(req.sortOrder(), newTask);

        Routine newRoutine = routine.withTasks(new TaskList(taskList));

        dataSource.putRoutine(newRoutine);
    }

    public void deleteTask(DeleteTaskRequest req) {
        dataSource.removeTask(req.routineId(), req.taskId());
    }

    public void editRoutineName(EditRoutineRequest req) {
        Routine routine = Objects.requireNonNull(find(req.routineId()).getValue());
        Routine newRoutine = routine.withName(req.routineName());

        dataSource.putRoutine(newRoutine);
    }
}
