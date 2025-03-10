package edu.ucsd.cse110.habitizer.app.ui.main.state;

import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class AppSubject extends Subject<AppState> {
    public AppSubject(RoutineState rs, TimerState ts) {
        super();
        setValue(new AppState(rs, ts));
    }

    public void updateRoutineState(RoutineState newRoutineState) {
        assert getValue() != null;
        setValue(new AppState(newRoutineState, getValue().timerState()));
    }

    public void updateTimerState(TimerState newTimerState) {
        assert getValue() != null;
        setValue(new AppState(getValue().routineState(), newTimerState));
    }
}
