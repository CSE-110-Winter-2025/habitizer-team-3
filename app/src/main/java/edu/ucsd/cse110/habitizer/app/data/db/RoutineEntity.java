package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskList;

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

    public @NonNull Routine toRoutine() {
        return new Routine(
                this.id,
                this.name,
                new TaskList(List.of()),
                this.time
        );
    }
    public @NonNull Routine toRoutine(@NonNull List<TaskEntity> taskEntities) {
        var tasks = taskEntities.stream()
                .map(TaskEntity::toTask)
                .collect(Collectors.toList());
        var taskList = new TaskList(tasks);
        return new Routine(this.id, this.name, taskList, this.time);
    }
}
