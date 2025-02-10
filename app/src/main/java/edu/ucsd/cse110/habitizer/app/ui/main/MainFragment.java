package edu.ucsd.cse110.habitizer.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the View
        this.view = FragmentMainBinding.inflate(inflater, container, false);

        view.taskList.setAdapter(adapter);

        // Listen for time field changes (focus lost => update the model)
        view.time.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // The user finished editing "time" vvv
                String newTimeStr = view.time.getText().toString();
                if (!newTimeStr.isEmpty()) {
                    int newTime = Integer.parseInt(newTimeStr);
                    var routines = activityModel.getAllRoutines().getValue();
                    if (routines == null) return;
                    var routine = routines.get(0);

                    var updateRoutine = routine.withTime(newTime);

                    activityModel.updateRoutine(updateRoutine);
                }
            }
        });

        return view.getRoot();
    }

    public void onViewCreated(@NonNull View view2, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view2, savedInstanceState);

        // 4) Observe routines AFTER the view is ready, so we can safely update UI
        activityModel.getAllRoutines().observe(routines -> {
            if (routines == null) return;

            var morningRoutine = routines.get(0);

            // Show the routine name in a TextView
            view.routineName.setText(morningRoutine.name());
            view.time.setText(morningRoutine.time().toString());

            // If you have a time field on the Routine, display it in the EditText
            // e.g. binding.time.setText(String.valueOf(morningRoutine.time()));

            // Show tasks in the adapter
            var tasks = morningRoutine.tasks();

            adapter.clear();
            adapter.addAll(tasks);
            adapter.notifyDataSetChanged();
        });
    }
}
