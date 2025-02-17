package edu.ucsd.cse110.habitizer.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class TimerViewModel extends ViewModel {
    // Current elapsed time in seconds
    private final MutableLiveData<Integer> elapsedSeconds = new MutableLiveData<>(0);

    private final MutableLiveData<Integer> taskTime = new MutableLiveData<>(0);

    public LiveData<Integer> getTaskTime() {
        return taskTime;
    }

    private Timer timer;
private int lastTaskEndTime = 0;

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
            elapsedSeconds.postValue(0);
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

    public int checkOffTask() {
        Integer current = elapsedSeconds.getValue();
        if (current == null) {
            current = 0;
        }
        int taskDuration = current - lastTaskEndTime;
        taskTime.postValue(taskDuration);
        lastTaskEndTime = current;
        return Math.max(taskDuration, 1);

    }
    public void resetPrevTaskTime(int currentElapsed) {
        lastTaskEndTime = currentElapsed;
    }
}
