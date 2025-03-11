package edu.ucsd.cse110.habitizer.app.data.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

@Entity(
        tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = RoutineEntity.class,
                parentColumns = "id",
                childColumns = "routineId",
                onDelete = ForeignKey.CASCADE
        )
)
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "routineId")
    public int routineId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "sortOrder")
    public Integer sortOrder;

    @ColumnInfo(name = "checkedOff")
    public boolean checkedOff;

    @ColumnInfo(name = "taskTime")
    public Integer taskTime;

    public TaskEntity() {
        // Default
    }

    public TaskEntity(int routineId, String name, Integer sortOrder, boolean checkedOff, Integer taskTime) {
        this.routineId = routineId;
        this.name = name;
        this.sortOrder = sortOrder;
        this.checkedOff = checkedOff;
        this.taskTime = taskTime;
    }

    public static TaskEntity fromDomain(@NonNull Task domain, int routineId) {
        Log.d("TaskEntity", "Creating TaskEntity from Task ID: " + domain.id() +
                ", Name: " + domain.name() + ", for Routine: " + routineId);

        TaskEntity entity = new TaskEntity();
        entity.id        = domain.id();
        entity.name      = domain.name();
        entity.sortOrder = domain.sortOrder();
        entity.checkedOff = domain.isCheckedOff();
        entity.taskTime  = domain.taskTime();
        entity.routineId = routineId;

        Log.d("TaskEntity", "Created entity with ID: " + entity.id +
                ", routineId: " + entity.routineId);

        return entity;
    }

    public Task toDomain() {
        Task t = new Task(
                this.id,
                this.name,
                this.sortOrder,
                this.taskTime
        );
        t.setCheckedOff(this.checkedOff);
        return t;
    }
}
