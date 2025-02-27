package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RoutineWithTasksEntity {
    @Embedded
    public RoutineEntity routine;

    @Relation(
            parentColumn = "id",
            entityColumn = "routine_id"
    )
    public List<TaskEntity> tasks;
}
