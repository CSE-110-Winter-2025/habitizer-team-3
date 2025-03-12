package edu.ucsd.cse110.habitizer.app;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class TimerViewModel extends ViewModel {
    // Current elapsed time in seconds
    private final MutableLiveData<Integer> elapsedSeconds = new MutableLiveData<>(0);

    private Timer timer;
    private boolean isPaused = false;
    private boolean isMocking = false;

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
            scheduleTimerTask();
        }
    }

    public void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            isPaused = true;
        }
    }

    public void resumeTimer() {
        if (isPaused && timer == null) {
            isPaused = false;
            timer = new Timer();
            scheduleTimerTask();
        }
    }

    // Safely update LiveData from a background thread
    private void scheduleTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            Integer currentValue = elapsedSeconds.getValue();
            if (currentValue == null) currentValue = 0;
            elapsedSeconds.postValue(currentValue + 1);
            }
        }, 1000, 1000);
    }
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isMocking = true;
        isPaused = true;
    }

    public void forwardTimer() {
        if (isMocking) {
            Integer currentValue = elapsedSeconds.getValue();
            if (currentValue == null) currentValue = 0;
            elapsedSeconds.postValue(currentValue + 15);
        }
    }

    // The Fragment/Activity can observe this LiveData
    public LiveData<Integer> getElapsedSeconds() {
        return elapsedSeconds;
    }
    public boolean isPaused() { return isPaused; }
}
