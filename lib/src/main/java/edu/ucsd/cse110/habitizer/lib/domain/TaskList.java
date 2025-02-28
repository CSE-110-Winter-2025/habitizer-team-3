package edu.ucsd.cse110.habitizer.lib.domain;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class TaskList implements Serializable {
    private final List<Task> tasks;
    private Integer lastTaskCheckoffTime = 0;
    private Integer currentTaskId = 0;

    public TaskList() { this.tasks = new ArrayList<>(); }

    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> tasks() {
        return tasks;
    }

    public Integer lastTaskCheckoffTime() {
        return lastTaskCheckoffTime;
    }

    public Integer currentTaskId() {
        return currentTaskId;
    }

    public void setLastCheckoffTime(Integer time) {
        lastTaskCheckoffTime = time;
    }
    public void setCurrentTaskId(Integer taskId) {
        currentTaskId = taskId;
    }

    public boolean allTasksChecked() {
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t != null && !t.isCheckedOff()) {
                return false;
            }
        }
        return true;
    }
}
