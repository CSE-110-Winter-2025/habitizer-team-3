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
    private final int routineId;
    private boolean routineInProgress = false;

    public TaskAdapter(Context context, List<Task> tasks, int routineId, Consumer<EditTaskDialogParams> onEditClick) {
        super(context, 0, new ArrayList<>(tasks));
        this.onEditClick = onEditClick;
        this.routineId = routineId;
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

        if (routineInProgress) {
            binding.taskEditButton.setVisibility(View.GONE);
            binding.taskCheckbox.setVisibility(View.VISIBLE);
        } else {
            binding.taskEditButton.setVisibility(View.VISIBLE);
            binding.taskCheckbox.setVisibility(View.GONE);
        }

        // listen for checking off a task
        binding.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCheckedOff(isChecked);

            // strikethrough the task name if it is checked off
            binding.taskName.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
            notifyDataSetChanged();
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
        notifyDataSetChanged();
    }
}
