package edu.ucsd.cse110.habitizer.lib.domain;

import java.io.Serializable;

public class App implements Serializable {
    private RoutineState routineState = RoutineState.BEFORE;
    private TimerState timerState = TimerState.REAL;
    private Integer timerTime;
    private Integer taskTime;
    private Integer currentRoutineId;
    public App(RoutineState rs, TimerState ts, Integer timerTime, Integer taskTime, Integer currentRoutineId) {
        this.routineState = rs;
        this.timerState = ts;
        this.timerTime = timerTime;
        this.taskTime = taskTime;
        this.currentRoutineId = currentRoutineId;
    }

    public RoutineState routineState() {
        return routineState;
    }

    public TimerState timerState() {
        return timerState;
    }

    public Integer timerTime() {
        return timerTime;
    }

    public Integer taskTime() {
        return taskTime;
    }
    public Integer currentRoutineId() {
        return currentRoutineId;
    }
}
