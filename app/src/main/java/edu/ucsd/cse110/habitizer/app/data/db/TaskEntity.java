package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "task_time")
    public int taskTime;

    @ColumnInfo(name = "routine_id")
    public long routineId;

    TaskEntity(@NonNull String name, int sortOrder) {
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public static TaskEntity fromTask(@NonNull Task task) {
        var taskEntity = new TaskEntity(task.name(), task.sortOrder());
        taskEntity.id = task.id();
        return taskEntity;
    }

    public @NonNull Task toTask() {
        return new Task(id, name, sortOrder, taskTime);
    }
}
