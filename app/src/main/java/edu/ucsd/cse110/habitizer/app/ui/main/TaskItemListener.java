package edu.ucsd.cse110.habitizer.app.ui.main;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

public interface TaskItemListener {
    void onEditClicked(Task task);
    void onCheckOffClicked(Task task);
    void onAllTaskCheckedOff();
}
