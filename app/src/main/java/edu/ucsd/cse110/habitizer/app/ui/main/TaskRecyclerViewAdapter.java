package edu.ucsd.cse110.habitizer.app.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskList;
import edu.ucsd.cse110.habitizer.app.ui.main.updaters.UITaskUpdater;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {
    private final TaskList taskList;
    private final TaskItemListener listener;
    private final UITaskUpdater taskUpdater;
    boolean flagDragButtonTouch = false;

    public TaskRecyclerViewAdapter(TaskList taskList, TaskItemListener listener, UITaskUpdater taskUpdater) {
        this.taskList = taskList;
        this.listener = listener;
        this.taskUpdater = taskUpdater;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        CheckBox checkBox;
        ImageButton editButton;
        TextView leftBracket;
        TextView rightBracket;
        ImageButton reorderButton;
        ImageButton deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.task_name);
            time = itemView.findViewById(R.id.task_time);
            checkBox = itemView.findViewById(R.id.task_checkbox);
            editButton = itemView.findViewById(R.id.task_edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            leftBracket = itemView.findViewById(R.id.task_time_left_bracket);
            rightBracket = itemView.findViewById(R.id.task_time_right_bracket);
            reorderButton = itemView.findViewById(R.id.task_sort_button);
        }

        public TextView name() {
            return name;
        }
        public TextView time() {
            return time;
        }
        public CheckBox checkBox() {
            return checkBox;
        }
        public ImageButton editButton() {
            return editButton;
        }
        public ImageButton deleteButton() { return deleteButton; }
        public TextView leftBracket() {
            return leftBracket;
        }
        public TextView rightBracket() {
            return rightBracket;
        }
        public ImageButton reorderButton() { return reorderButton; }
    }

    public void exchangeOrder(int from, int to) {
        Log.wtf("#", "exchangeOrder: " + taskList.tasks().get(from).name() + " & " + taskList.tasks().get(to).name());
        taskList.exchangeOrder(from, to);
    }



    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.tasks().get(position);
        TextView name = holder.name();
        TextView time = holder.time();
        CheckBox checkBox = holder.checkBox();
        ImageButton editButton = holder.editButton();
        ImageButton deleteButton = holder.deleteButton();
        TextView leftBracket = holder.leftBracket();
        TextView rightBracket = holder.rightBracket();
        ImageButton reorderButton = holder.reorderButton();

        reorderButton.setVisibility(taskUpdater.canReorder() ? View.VISIBLE : View.GONE);

        reorderButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Log.wtf("#", "MotionEvent.ACTION_DOWN");
                        flagDragButtonTouch = true;
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        Log.wtf("#", "MotionEvent.ACTION_UP/ACTION_CANCEL");
                        flagDragButtonTouch = false;
                        break;
                    }
                }
                return true;
            }
        });

        name.setText(task.name());

        String timeText = " "; // default to nothing

        if (task.isCheckedOff() && task.taskTime() != null) { // task is checked off, show the time
            timeText = task.taskTime() + " m";
        } else if (task.sortOrder() < taskList.currentTaskId()) { // task is skipped
            timeText = "-";
        }

        time.setText(timeText);

        checkBox.setChecked(task.isCheckedOff());
        name.setPaintFlags(task.isCheckedOff() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);

        checkBox.setVisibility(taskUpdater.canCheckoff() ? View.VISIBLE : View.GONE);
        checkBox.setEnabled(!task.isCheckedOff() && taskUpdater.isCheckoffEnabled());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) listener.onCheckOffClicked(task);
        });

        editButton.setOnClickListener(v -> {
            listener.onEditClicked(task);
        });

        deleteButton.setOnClickListener((v -> {
            listener.onDeleteClicked(task);
        }));

        if (taskList.allTasksChecked()) listener.onAllTaskCheckedOff();

        editButton.setVisibility(taskUpdater.canEdit() ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(taskUpdater.canDelete() ? View.VISIBLE : View.GONE);

        leftBracket.setVisibility(taskUpdater.showBrackets() ? View.VISIBLE : View.GONE);
        rightBracket.setVisibility(taskUpdater.showBrackets() ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.tasks().size();
    }
}
