package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private final int sortOrder;
    private boolean checkedOff = false;

    public Task(@Nullable Integer id, @NonNull String name, int sortOrder) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id() { return id; }

    public @NonNull String name() { return name; }

    public int sortOrder() { return sortOrder; }

    public boolean isCheckedOff() {
        return checkedOff;
    }

    public void setCheckedOff(boolean checkedOff) {
        this.checkedOff = checkedOff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return sortOrder == task.sortOrder && Objects.equals(id, task.id) && Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sortOrder);
    }

    public Task withIdAndSortOrder(int id, int sortOrder) {
        return new Task(id, this.name, sortOrder);
    }
}
