package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;


public class Routine implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private @NonNull TaskList taskList;
    private final @Nullable Integer time;

    public Routine(@Nullable Integer id, @NonNull String name, @NonNull TaskList taskList, @Nullable Integer time) {
        this.id = id;
        this.name = name;
        this.taskList = taskList;
        this.time = time;
    }

    public void commitTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public @Nullable Integer id() { return id; }

    public @NonNull String name() { return name; }

    public @NonNull TaskList taskList() { return taskList; }

    public @Nullable Integer time() { return time; }

    public Routine withId(int id) {
        return new Routine(id, this.name, this.taskList, this.time);
    }

    public Routine withTime(int time) {
        return new Routine(this.id, this.name, this.taskList, time);
    }

    public Routine withTasks(TaskList tasks) {
        return new Routine(this.id, this.name, tasks, this.time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id) && Objects.equals(name, routine.name) && Objects.equals(taskList, routine.taskList) && Objects.equals(time, routine.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskList);
    }
}
