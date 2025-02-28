package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoutineBuilder {
    private @Nullable Integer id = null;
    private @NonNull String name = "New Routine";
    private @NonNull TaskList taskList = new TaskList();
    private @Nullable Integer time = 30;

    public RoutineBuilder setId(@Nullable Integer id) {
        this.id = id;
        return this;
    }

    public RoutineBuilder setName(@NonNull String name) {
        this.name = name;
        return this;
    }

    public RoutineBuilder setTaskList(@NonNull TaskList taskList) {
        this.taskList = taskList;
        return this;
    }

    public RoutineBuilder setTime(@Nullable Integer time) {
        this.time = time;
        return this;
    }

    public Routine build() {
        return new Routine(
                this.id,
                this.name,
                this.taskList,
                this.time
        );
    }
}
