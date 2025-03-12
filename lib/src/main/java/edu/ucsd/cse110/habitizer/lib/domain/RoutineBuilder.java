package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RoutineBuilder {
    private @NonNull Integer id = -1;
    private @NonNull String name = "New Routine";
    private @NonNull TaskList taskList = new TaskList(new ArrayList<>());
    private @Nullable Integer time = 30;

    public RoutineBuilder setId(Integer id) {
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
