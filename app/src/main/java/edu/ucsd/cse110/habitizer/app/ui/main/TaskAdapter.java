package edu.ucsd.cse110.habitizer.app.ui.main;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineState;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskAdapter extends ArrayAdapter<Task> {
    public long lastTaskEndTime = 0;
    public int lastTaskCheckedSortOrder = -1;
    private TaskItemListener taskItemListener;
    private RoutineState routineState = RoutineState.BEFORE;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, new ArrayList<>(tasks));
    }

    public void setTaskItemListener(TaskItemListener listener) {
        this.taskItemListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get task for this position
        var task = getItem(position);
        assert task != null;

        // check if view being reused
        ListItemTaskBinding binding;
        if (convertView != null) {
            // if so bind it
            binding = ListItemTaskBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from out layout XML
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemTaskBinding.inflate(layoutInflater, parent, false);
        }

        // populate the view with routine's data.
        binding.taskName.setText(task.name());

        //Check if the task is before or after the checked-off task
        if (task.sortOrder() < lastTaskCheckedSortOrder && !task.isCheckedOff()) {
            binding.textTaskTime.setText("-");
        } else if (task.taskTime() == -1) {
            binding.textTaskTime.setText(" ");
        } else if (task.isCheckedOff()){
            binding.textTaskTime.setText(task.taskTime() + "m");
        }

        switch(routineState) {
            case BEFORE:
                binding.taskCheckbox.setVisibility(View.GONE);
                break;
            case DURING:
                binding.taskCheckbox.setVisibility(View.VISIBLE);
                binding.taskEditButton.setVisibility(View.GONE);
                binding.taskCheckbox.setEnabled(!task.isCheckedOff());
                break;
            case AFTER:
                binding.taskCheckbox.setEnabled(false);
                break;
        }

        binding.taskEditButton.setOnClickListener(v -> {
            if (taskItemListener != null) {
                taskItemListener.onEditClicked(task);
            }
        });

        binding.taskCheckbox.setOnCheckedChangeListener(null);
        binding.taskCheckbox.setChecked(task.isCheckedOff());
        binding.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.taskName.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
            if (isChecked) {
                if (taskItemListener != null) {
                    taskItemListener.onCheckOffClicked(task);
                }
                lastTaskCheckedSortOrder = task.sortOrder();
                notifyDataSetChanged();

                // Check if all tasks are checked off
                if (allTasksChecked() && taskItemListener != null) {
                    taskItemListener.onAllTaskCheckedOff();
                }
            } else {
                buttonView.setChecked(true);
            }
        });

        return binding.getRoot();
    }

    private boolean allTasksChecked() {
        for (int i = 0; i < getCount(); i++) {
            Task t = getItem(i);
            if (t != null && !t.isCheckedOff()) {
                return false;
            }
        }
        return true;
    }

    public void updateTask(Task prevTask, Task newTask) {
        int index = getPosition(prevTask);
        remove(prevTask);
        insert(newTask, index);
        notifyDataSetChanged();
    }

    // good practice
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var t = getItem(position);
        assert t != null;

        var id = t.id();
        assert id != null;

        return id;
    }

    public void onStartButtonPressed() {
        routineState = RoutineState.DURING;
        lastTaskEndTime = System.currentTimeMillis()/1000;
        notifyDataSetChanged();
    }

    public void onEndRoutine() {
        routineState = RoutineState.AFTER;
        notifyDataSetChanged();
    }
}
