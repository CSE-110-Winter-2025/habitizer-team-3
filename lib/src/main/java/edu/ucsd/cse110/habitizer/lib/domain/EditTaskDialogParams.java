package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public record EditTaskDialogParams(@NonNull Integer routineId, @NonNull Integer taskId, @NonNull Integer sortOrder, @Nullable Integer taskTime) {
}
