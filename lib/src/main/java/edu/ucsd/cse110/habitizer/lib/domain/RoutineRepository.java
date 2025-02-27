package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.util.Subject;

public interface RoutineRepository {
    Integer count();

    Subject<Routine> find(int id);

    Subject<List<Routine>> findAll();

    void save(Routine routine);

    void addTaskToRoutine(int routineId, @NonNull Task task);

    void editTask(EditTaskRequest req);
}
