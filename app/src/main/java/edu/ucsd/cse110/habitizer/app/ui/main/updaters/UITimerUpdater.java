package edu.ucsd.cse110.habitizer.app.ui.main.updaters;

import androidx.annotation.Nullable;

import edu.ucsd.cse110.habitizer.app.ui.main.state.AppState;
import edu.ucsd.cse110.habitizer.app.ui.main.state.TimerState;
import edu.ucsd.cse110.habitizer.lib.domain.App;
import edu.ucsd.cse110.habitizer.lib.util.Observer;

public class UITimerUpdater implements Observer<App> {
    private boolean showStop;
    private boolean showEnd;
    private boolean showFastForward;
    private boolean showPauseResume;
    public boolean showStop() {
        return showStop;
    }


    public boolean showEnd() {
        return showEnd;
    }

    public boolean showFastForward() {
        return showFastForward;
    }

    public boolean showPauseResume() { return showPauseResume; }

    @Override
    public void onChanged(@Nullable App value) {
        if (value == null) return;

        switch (value.timerState()) {
            case REAL:
                showStop = true;
                showEnd = true;
                showFastForward = false;
                showPauseResume = true;
                break;

            case MOCK:
                showStop = false;
                showEnd = true;
                showFastForward = true;
                showPauseResume = true;
                break;
            default:
                break;
        }
    }
}

