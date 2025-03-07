package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public record EditRoutineRequest(@NonNull Integer routineId, @NonNull String routineName) {
}
