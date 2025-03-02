package edu.ucsd.cse110.habitizer.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TimerViewModel extends ViewModel {
    // Current elapsed time in seconds
    private final MutableLiveData<Integer> elapsedSeconds = new MutableLiveData<>(0);

    private Timer timer;
    private boolean isPaused = false;

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
            if (!isPaused) {
                elapsedSeconds.postValue(0);
            }
            isPaused = false;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Safely update LiveData from a background thread
                    Integer currentValue = elapsedSeconds.getValue();
                    if (currentValue == null) currentValue = 0;
                    elapsedSeconds.postValue(currentValue + 1);
                }
            }, 1000, 1000); // 1 second delay, repeat every 1 second
        }
    }

    public void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            isPaused = true;
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void forwardTimer() {
        Integer currentValue = elapsedSeconds.getValue();
        if (currentValue == null) currentValue = 0;
        elapsedSeconds.postValue(currentValue + 30);
    }

    // The Fragment/Activity can observe this LiveData
    public LiveData<Integer> getElapsedSeconds() {
        return elapsedSeconds;
    }
}
