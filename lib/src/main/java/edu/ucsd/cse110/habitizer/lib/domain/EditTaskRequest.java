package edu.ucsd.cse110.habitizer.lib.domain;

public record EditTaskRequest(int routineId, int taskId, int sortOrder, String taskName) {
}
