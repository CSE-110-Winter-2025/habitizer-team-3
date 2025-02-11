package edu.ucsd.cse110.habitizer.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentMainBinding view;
    private TaskAdapter adapter;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (empty for now)
        this.adapter = new TaskAdapter(requireContext(), List.of());
        activityModel.getAllRoutines().observe(routines -> {
            if (routines == null) return;

            var morningRoutine = routines.get(0);
            var tasks = morningRoutine.tasks();

            adapter.clear();
            adapter.addAll(tasks);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the View
        this.view = FragmentMainBinding.inflate(inflater, container, false);

        view.routineName.setText("Morning");
        view.taskList.setAdapter(adapter);

        view.addTaskButton.setOnClickListener(v -> {
            DialogFragment addTaskFragment = new AddTaskDialogFragment();
            addTaskFragment.show(getActivity().getSupportFragmentManager(), "add task dialog");
        });

        return view.getRoot();
    }
}
