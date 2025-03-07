package edu.ucsd.cse110.habitizer.app.ui.main.updaters;



import androidx.annotation.Nullable;
import edu.ucsd.cse110.habitizer.app.ui.main.state.TimerState;
import edu.ucsd.cse110.habitizer.lib.util.Observer;

public class UITimerUpdater implements Observer<TimerState> {
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
    public void onChanged(@Nullable TimerState value) {
        if (value == null) return;
        switch (value) {
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

