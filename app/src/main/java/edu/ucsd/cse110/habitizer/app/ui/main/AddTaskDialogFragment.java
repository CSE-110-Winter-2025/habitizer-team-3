package edu.ucsd.cse110.habitizer.app.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import edu.ucsd.cse110.habitizer.app.R;

public class AddTaskDialogFragment extends DialogFragment {

    public interface AddTaskDialogListener {
        void onTaskAdded(String taskName);
    }

    private AddTaskDialogListener listener;

    public void setAddTaskDialogListener(AddTaskDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_task_dialog, null);

        // Get references to the views
        EditText taskInput = view.findViewById(R.id.task_name_input);
        Button addButton = view.findViewById(R.id.add_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Create the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(view);

        // Set up the add button
        addButton.setOnClickListener(v -> {
            String taskName = taskInput.getText().toString().trim();
            if (!taskName.isEmpty()) {
                // Notify the listener that a task was added
                if (listener != null) {
                    listener.onTaskAdded(taskName);
                }
                dismiss(); // Close the dialog
            } else {
                taskInput.setError("Task name cannot be empty");
            }
        });

        // Set up the cancel button
        cancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}