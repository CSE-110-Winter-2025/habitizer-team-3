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
    private boolean canSwap;
    private boolean canEditTime;
    private boolean showPauseResume;

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

    public boolean showAdd() {
        return showAdd;
    }

    public boolean canSwap() {
        return canSwap;
    }

    public boolean canEditTime() {
        return canEditTime;
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
                canSwap = true;
                canEditTime = true;
                showPauseResume = false;
                break;

            case DURING:
                showStart = false;
                showStop = true;
                showEnd = true;
                showFastForward = true;
                showAdd = false;
                canSwap = false;
                canEditTime = false;
                showPauseResume = true;
                break;

            case AFTER:
                showStart = true;
                showStop = false;
                showEnd = false;
                showFastForward = false;
                showAdd = false;
                canSwap = false;
                canEditTime = false;
                showPauseResume = false;
                break;
            default:
                break;
        }
    }
}
