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
import edu.ucsd.cse110.habitizer.app.databinding.FragmentEditRoutineDialogBinding;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineDialogParams;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineRequest;

public class EditRoutineDialogFragment extends DialogFragment {
    private MainViewModel activityModel;
    private @NonNull FragmentEditRoutineDialogBinding view;
    private static final String ARG_ROUTINE_ID = "routine_id";
    private int routineId;

    EditRoutineDialogFragment() {
        // Required empty public constructor
    }

    public static EditRoutineDialogFragment newInstance(EditRoutineDialogParams params) {
        var fragment = new EditRoutineDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROUTINE_ID, params.routineId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        this.routineId = requireArguments().getInt(ARG_ROUTINE_ID);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceBundle) {
        // change if we implement custom fragment for this
        this.view = FragmentEditRoutineDialogBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Edit Routine Name")
                .setMessage("Please provide the new routine name.")
                .setView(view.getRoot())
                .setPositiveButton("Update", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var routineName = view.routineNameInput.getText().toString().trim();
        EditRoutineRequest req = new EditRoutineRequest(routineId, routineName);
        activityModel.editRoutine(req);

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}