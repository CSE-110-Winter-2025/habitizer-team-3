package edu.ucsd.cse110.habitizer.app.ui.main.updaters;

import androidx.annotation.Nullable;

import edu.ucsd.cse110.habitizer.app.ui.main.state.AppState;
import edu.ucsd.cse110.habitizer.app.ui.main.state.RoutineState;
import edu.ucsd.cse110.habitizer.lib.util.Observer;

public class UIRoutineUpdater implements Observer<AppState> {
    private boolean showStop;
    private boolean showStart;
    private boolean showEnd;
    private boolean showFastForward;
    private boolean showAdd;
    private boolean showPauseResume;
    private boolean showCreateRoutine;
    private boolean canEditRoutine;

    public boolean showStop() {
        return showStop;
    }
    public boolean showStart() {
        return showStart;
    }
    public boolean showEnd() {
        return showEnd;
    }
    public boolean showFastForward() {
        return showFastForward;
    }
    public boolean showCreateRoutine() {
        return showCreateRoutine;
    }
    public boolean showAdd() {
        return showAdd;
    }
    public boolean canEditRoutine() {
        return canEditRoutine;
    }
    public boolean showPause() { return showPauseResume; }

    @Override
    public void onChanged(@Nullable AppState value) {
        if (value == null) return;
        switch (value.routineState()) {
            case BEFORE:
                showStart = true;
                showStop = false;
                showEnd = false;
                showFastForward = false;
                showAdd = true;
                showPauseResume = false;
                showCreateRoutine = true;
                canEditRoutine = true;
                break;

            case DURING:
                showStart = false;
                showStop = true;
                showEnd = true;
                showFastForward = true;
                showAdd = false;
                showPauseResume = true;
                showCreateRoutine = false;
                canEditRoutine = false;
                break;

            case AFTER:
                showStart = true;
                showStop = false;
                showEnd = false;
                showFastForward = false;
                showAdd = false;
                showPauseResume = false;
                showCreateRoutine = false;
                canEditRoutine = false;

                break;
            default:
                break;
        }
    }
}
