package edu.ucsd.cse110.habitizer.lib.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class TaskList implements Serializable {
    private final List<Task> tasks;
    private Integer lastTaskCheckoffTime = 0;
    private Integer currentTaskId = 0;

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public List<Task> tasks() {
        return tasks;
    }

    public int count() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
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


    public void exchangeOrder(int from, int to) {
        int fromSortOrder = tasks.get(from).sortOrder();
        int toSortOrder = tasks.get(to).sortOrder();
        tasks.get(from).setSortOrder(toSortOrder);
        tasks.get(to).setSortOrder(fromSortOrder);

        tasks.sort(Comparator.comparingInt(Task::sortOrder));
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
