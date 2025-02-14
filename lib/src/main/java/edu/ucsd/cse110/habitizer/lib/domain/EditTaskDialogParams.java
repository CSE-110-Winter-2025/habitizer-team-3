package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

public record EditTaskDialogParams(@NonNull Integer routineId, @NonNull Integer taskId, @NonNull Integer sortOrder) {
}
