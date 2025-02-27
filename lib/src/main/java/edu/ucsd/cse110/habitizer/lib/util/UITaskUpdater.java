package edu.ucsd.cse110.habitizer.lib.util;

import androidx.annotation.Nullable;

import edu.ucsd.cse110.habitizer.lib.domain.RoutineState;

public class UITaskUpdater implements UIUpdater {
    private boolean canEdit = true;
    private boolean canCheckoff = false;
    private boolean showBrackets = true;
    private boolean isCheckoffEnabled = false;

    public boolean canEdit() {
        return canEdit;
    }

    public boolean canCheckoff() {
        return canCheckoff;
    }

    public boolean isCheckoffEnabled() {
        return isCheckoffEnabled;
    }

    public boolean showBrackets() {
        return showBrackets;
    }
    @Override
    public void onChanged(@Nullable RoutineState value) {
        if (value == null) return;
        switch (value) {
            case BEFORE:
                canEdit = true;
                canCheckoff = false;
                isCheckoffEnabled = false;
                showBrackets = true;
                break;

            case DURING:
                canEdit = false;
                canCheckoff = true;
                isCheckoffEnabled = true;
                showBrackets = false;
                break;

            case AFTER:
                canEdit = false;
                canCheckoff = true;
                isCheckoffEnabled = false;
                showBrackets = false;
                break;
        }
    }
}
