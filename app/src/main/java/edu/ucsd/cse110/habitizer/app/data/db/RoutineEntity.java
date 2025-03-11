package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.TaskList;

@Entity(tableName = "routines")
public class RoutineEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "time")
    public Integer time;

    public RoutineEntity() {
        // Default
    }


    RoutineEntity(@NonNull String name, Integer time) {
        this.name = name;
        this.time = time;
    }

    public static RoutineEntity fromRoutine(@NonNull Routine routine) {
        RoutineEntity entity = new RoutineEntity(routine.name(), routine.time());
        entity.id = routine.id();
        return entity;
    }

    public @NonNull Routine toRoutine() {
        return new Routine(this.id, this.name, new TaskList(List.of()), this.time);
    }
}
