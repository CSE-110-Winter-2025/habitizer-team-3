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
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;

public class EditTaskDialogFragment extends DialogFragment {
    private MainViewModel activityModel;
    private FragmentAddTaskDialogBinding view;
    private static final String ARG_ROUTINE_ID = "routine_id";
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_TASK_SORT_ORDER = "task_sort_order";
    private static final String ARG_TASK_TIME = "task_time";
    private int routineId;
    private int taskId;
    private int taskSortOrder;
    private Integer taskTime;

    EditTaskDialogFragment() {
        // Required empty public constructor
    }

    public static EditTaskDialogFragment newInstance(EditTaskDialogParams params) {
        var fragment = new EditTaskDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROUTINE_ID, params.routineId());
        args.putInt(ARG_TASK_ID, params.taskId());
        args.putInt(ARG_TASK_SORT_ORDER, params.sortOrder());
        args.putSerializable(ARG_TASK_TIME, params.taskTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        this.routineId = requireArguments().getInt(ARG_ROUTINE_ID);
        this.taskId = requireArguments().getInt(ARG_TASK_ID);
        this.taskSortOrder = requireArguments().getInt(ARG_TASK_SORT_ORDER);
        this.taskTime = (Integer) requireArguments().getInt(ARG_TASK_TIME);

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
                .setTitle("Edit Task")
                .setMessage("Please provide the new task name.")
                .setView(view.getRoot())
                .setPositiveButton("Update", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var taskName = view.taskNameInput.getText().toString().trim();
        EditTaskRequest req = new EditTaskRequest(routineId, taskId, taskSortOrder, taskName, taskTime);
        activityModel.editTask(req);

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}