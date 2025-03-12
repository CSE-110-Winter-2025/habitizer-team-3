package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.App;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineState;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;

@Entity(tableName = "app")
public class AppEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routineState")
    public RoutineState routineState = RoutineState.BEFORE;

    @ColumnInfo(name = "timerState")
    public TimerState timerState = TimerState.REAL;

    @ColumnInfo(name = "timerTime")
    public Integer timerTime;

    @ColumnInfo(name = "taskTime")
    public Integer taskTime;

    @ColumnInfo(name = "currentRoutineId")
    public Integer currentRoutineId;

    public AppEntity() {
        // Default
    }


    AppEntity(@NonNull RoutineState rs, TimerState ts, Integer timerTime, Integer taskTime, Integer cri) {
        this.routineState = rs;
        this.timerState = ts;
        this.timerTime = timerTime;
        this.taskTime = taskTime;
        this.currentRoutineId = cri;
    }

    public static AppEntity fromApp(@NonNull App App) {
        return new AppEntity(App.routineState(), App.timerState(), App.timerTime(), App.taskTime(), App.currentRoutineId());
    }

    public @NonNull App toApp() {
        return new App(this.routineState,this.timerState, this.timerTime, this.taskTime, this.currentRoutineId);
    }
}
