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
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, new ArrayList<>(tasks));
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

        // listen for checking off a task
        binding.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCheckedOff(isChecked);

            // strikethrough the task name if it is checked off
            binding.taskName.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
            notifyDataSetChanged();
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
}
