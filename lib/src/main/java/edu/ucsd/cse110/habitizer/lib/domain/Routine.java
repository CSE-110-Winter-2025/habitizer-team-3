package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Routine implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private final @NonNull List<Task> tasks;
    private final @Nullable Integer time;


    public Routine(@Nullable Integer id, @NonNull String name, @Nullable List<Task> tasks, @Nullable Integer time) {
        this.id = id;
        this.name = name;
        this.tasks = Objects.requireNonNullElseGet(tasks, ArrayList::new);
        this.time = time;
    }


    public @Nullable Integer id() { return id; }

    public @NonNull String name() { return name; }

    public @NonNull List<Task> tasks() { return tasks; }

    public @Nullable Integer time() { return time; }

    public Routine withId(int id) {
        return new Routine(id, this.name, this.tasks, this.time);
    }

    public Routine withTime(int time) {
        return new Routine(this.id, this.name, this.tasks, time);
    }

    public Routine withTasks(List<Task> tasks) {
        return new Routine(this.id, this.name, tasks, this.time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id) && Objects.equals(name, routine.name) && Objects.equals(tasks, routine.tasks) && Objects.equals(time, routine.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tasks);
    }
}
