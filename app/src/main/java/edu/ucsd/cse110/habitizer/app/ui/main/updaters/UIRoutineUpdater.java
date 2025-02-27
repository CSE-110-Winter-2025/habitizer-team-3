package edu.ucsd.cse110.habitizer.app.ui.main.updaters;

import androidx.annotation.Nullable;

import edu.ucsd.cse110.habitizer.lib.domain.RoutineState;
import edu.ucsd.cse110.habitizer.lib.util.UIUpdater;

public class UIRoutineUpdater implements UIUpdater {
    private boolean showStop;
    private boolean showStart;
    private boolean showEnd;
    private boolean showFastForward;
    private boolean showAdd;
    private boolean canSwap;
    private boolean canEditTime;

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
                canSwap = true;
                canEditTime = true;
                break;

            case DURING:
                showStart = false;
                showStop = true;
                showEnd = true;
                showFastForward = true;
                showAdd = false;
                canSwap = false;
                canEditTime = false;
                break;

            case AFTER:
                showStart = true;
                showStop = false;
                showEnd = false;
                showFastForward = false;
                showAdd = false;
                canSwap = false;
                canEditTime = false;
                break;
            default:
                break;
        }
    }
}
