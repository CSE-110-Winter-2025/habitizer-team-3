package edu.ucsd.cse110.habitizer.app.ui.main.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentAddTaskDialogBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class AddTaskDialogFragment extends DialogFragment {
    private MainViewModel activityModel;
    private FragmentAddTaskDialogBinding view;

    AddTaskDialogFragment() {
        // Required empty public constructor
    }

    public static AddTaskDialogFragment newInstance() {
        var fragment = new AddTaskDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceBundle) {
        this.view = FragmentAddTaskDialogBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Task")
                .setMessage("Please provide the new task name.")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        String taskName = view.taskNameInput.getText().toString().trim();
        Integer routineId = activityModel.getCurrentRoutineId();
        Routine currentRoutine = activityModel.getCurrentRoutine().getValue();
        Integer listSize = currentRoutine.taskList().tasks().size();
        Task task = new Task(null, taskName, listSize, null);

        activityModel.addTaskToRoutine(routineId, task);

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    @Override
    public void onDismiss(@Nullable DialogInterface dialog) {
        super.onDismiss(dialog);

        Bundle result = new Bundle();
        result.putBoolean("dialog_dismissed", true);
        getParentFragmentManager().setFragmentResult("ADD_TASK_DIALOG_DISMISSED", result);
    }
}