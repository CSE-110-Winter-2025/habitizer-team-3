package edu.ucsd.cse110.habitizer.app.ui.main.updaters;

import androidx.annotation.Nullable;

import edu.ucsd.cse110.habitizer.app.ui.main.state.AppState;
import edu.ucsd.cse110.habitizer.app.ui.main.state.RoutineState;
import edu.ucsd.cse110.habitizer.lib.util.Observer;

public class UITaskUpdater implements Observer<AppState> {
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCheckoff = false;
    private boolean showBrackets = true;
    private boolean isCheckoffEnabled = false;
    private boolean canReorder = true;

    public boolean canEdit() {
        return canEdit;
    }

    public boolean canDelete() { return canDelete; }

    public boolean canCheckoff() {
        return canCheckoff;
    }

    public boolean isCheckoffEnabled() {
        return isCheckoffEnabled;
    }

    public boolean showBrackets() {
        return showBrackets;
    }
    public boolean canReorder() { return canReorder; }
    @Override
    public void onChanged(@Nullable AppState value) {
        if (value == null) return;
        switch (value.routineState()) {
            case BEFORE:
                canEdit = true;
                canDelete = true;
                canCheckoff = false;
                isCheckoffEnabled = false;
                showBrackets = true;
                canReorder = true;
                break;

            case DURING:
                canEdit = false;
                canDelete = false;
                canCheckoff = true;
                isCheckoffEnabled = true;
                showBrackets = false;
                canReorder = false;
                break;

            case AFTER:
                canEdit = false;
                canDelete = false;
                canCheckoff = true;
                isCheckoffEnabled = false;
                showBrackets = false;
                canReorder = false;
                break;
        }
    }
}
