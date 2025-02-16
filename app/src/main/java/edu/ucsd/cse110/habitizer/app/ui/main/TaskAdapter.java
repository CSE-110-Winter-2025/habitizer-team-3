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
import java.util.Objects;
import java.util.function.Consumer;

import edu.ucsd.cse110.habitizer.app.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskAdapter extends ArrayAdapter<Task> {
    Consumer<EditTaskDialogParams> onEditClick;
    Consumer<Integer> onTaskTimeUpdate;
    private final int routineId;
    private boolean routineInProgress = false;
    public long lastTaskEndTime = 0;
    private int lastTaskCheckedSortOrder = -1;


    public TaskAdapter(Context context, List<Task> tasks, int routineId, Consumer<EditTaskDialogParams> onEditClick) {
        super(context, 0, new ArrayList<>(tasks));
        this.onEditClick = onEditClick;
        this.routineId = routineId;
    }

    public interface OnTaskCheckedChangeListener {
        void onTaskCheckedChanged(int position, boolean isChecked);
    }

    private OnTaskCheckedChangeListener listener;

    // Set the listener from MainFragment
    public void setOnTaskCheckedChangeListener(OnTaskCheckedChangeListener listener) {
        this.listener = listener;
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
        } else {
            binding.textTaskTime.setText(task.taskTime() + "m");
        }

        if (routineInProgress) {
            binding.taskEditButton.setVisibility(View.GONE);
            binding.taskCheckbox.setVisibility(View.VISIBLE);
        } else {
            binding.taskEditButton.setVisibility(View.VISIBLE);
            binding.taskCheckbox.setVisibility(View.GONE);
        }

        // listen for checking off a task
        binding.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                long currentTime = System.currentTimeMillis() / 1000;
                Integer taskTimeInSeconds = (int)(currentTime - lastTaskEndTime);
                Integer taskTimeInMinutes = (int) Math.ceil(taskTimeInSeconds / 60.0);
                Task updatedTask= task.withTime(taskTimeInMinutes);
                updatedTask.setCheckedOff(true);
                remove(task);
                insert(updatedTask, position);
                lastTaskCheckedSortOrder = updatedTask.sortOrder();
                binding.textTaskTime.setText(String.valueOf(updatedTask.taskTime()) + " m");
                for (int i = 0; i < position; i++) {
                    Task previousTask = getItem(i);
                    if (previousTask != null && !previousTask.isCheckedOff()) {
                        Task skippedTask = previousTask.withTime(null);
                        skippedTask.setCheckedOff(false);
                        remove(previousTask);
                        insert(skippedTask, i);

                    }
                }
                lastTaskEndTime = currentTime;
                notifyDataSetChanged();
            }
            binding.taskName.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        });


        // listen for editing a task
        binding.taskEditButton.setOnClickListener(v -> {
            var taskId = Objects.requireNonNull(task.id());
            var sortOrder = task.sortOrder();
            var taskTime = task.taskTime();

            EditTaskDialogParams params = new EditTaskDialogParams(routineId, taskId, sortOrder,taskTime);
            onEditClick.accept(params);
        });

        return binding.getRoot();
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
        routineInProgress = true;
        lastTaskEndTime = System.currentTimeMillis()/1000;
        notifyDataSetChanged();
    }
}
