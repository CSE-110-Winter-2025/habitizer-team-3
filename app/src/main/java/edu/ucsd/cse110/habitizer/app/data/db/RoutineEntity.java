package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;

@Entity(tableName = "routines")
public class RoutineEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "time")
    public Integer time;

    RoutineEntity(@NonNull String name, int time) {
        this.name = name;
        this.time = time;
    }

    public static RoutineEntity fromRoutine(@NonNull Routine routine) {
        var routineEntity = new RoutineEntity(routine.name(), routine.time());
        routineEntity.id = routine.id();
        return routineEntity;
    }
}
