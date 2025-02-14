package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

public record EditTaskRequest(@NonNull Integer routineId, @NonNull Integer taskId, @NonNull Integer sortOrder, @NonNull String taskName) {
}
