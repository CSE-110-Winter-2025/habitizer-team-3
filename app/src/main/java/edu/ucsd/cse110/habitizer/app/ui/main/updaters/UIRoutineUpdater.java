package edu.ucsd.cse110.habitizer.app.ui.main.updaters;

import androidx.annotation.Nullable;

import edu.ucsd.cse110.habitizer.app.ui.main.state.RoutineState;
import edu.ucsd.cse110.habitizer.lib.util.Observer;

public class UIRoutineUpdater implements Observer<RoutineState> {
    private boolean showStop;
    private boolean showStart;
    private boolean showEnd;
    private boolean showFastForward;
    private boolean showAdd;
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

    @Override
    public void onChanged(@Nullable RoutineState value) {
        if (value == null) return;
        switch (value) {
            case BEFORE:
                showStart = true;
                showStop = false;
                showEnd = false;
                showFastForward = false;
                showAdd = true;
                showCreateRoutine = true;
                canEditRoutine = true;
                break;

            case DURING:
                showStart = false;
                showStop = true;
                showEnd = true;
                showFastForward = true;
                showAdd = false;
                showCreateRoutine = false;
                canEditRoutine = false;
                break;

            case AFTER:
                showStart = true;
                showStop = false;
                showEnd = false;
                showFastForward = false;
                showAdd = false;
                showCreateRoutine = false;
                canEditRoutine = false;
                break;
            default:
                break;
        }
    }
}
