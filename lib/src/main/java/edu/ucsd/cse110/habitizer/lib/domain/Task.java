package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private final @NonNull Integer sortOrder;
    private boolean checkedOff = false;


    private final @Nullable Integer taskTime;

    public Task(@Nullable Integer id, @NonNull String name, @NonNull Integer sortOrder, @Nullable Integer taskTime) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.taskTime = taskTime != null ? taskTime : -1;
    }

    public @Nullable Integer id() { return id; }

    public @NonNull String name() { return name; }

    public int sortOrder() { return sortOrder; }

    public @Nullable Integer taskTime() { return taskTime;}

    public boolean isCheckedOff() {
        return checkedOff;
    }

    public void setCheckedOff(boolean checkedOff) {
        this.checkedOff = checkedOff;
    }

    public Task withTime(Integer taskTime) {
        return new Task(this.id, this.name, this.sortOrder, taskTime);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(sortOrder, task.sortOrder) && Objects.equals(id, task.id) && Objects.equals(name, task.name) && checkedOff == task.checkedOff;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sortOrder);
    }

    public Task withIdAndSortOrder(int id, int sortOrder) {
        return new Task(id, this.name, sortOrder, taskTime);
    }
}
