package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters.IncidentLogsAdapter;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.IncidentLogsViewModel;

public class IncidentLogsFragment extends Fragment {

    private IncidentLogsViewModel viewModel;
    private FloatingActionButton fab;
    private IncidentLogsAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.incident_logs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_incident_logs);
        fab = view.findViewById(R.id.fab);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(IncidentLogsViewModel.class);

        // Initialize the adapter
        adapter = new IncidentLogsAdapter();

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Observe LiveData from ViewModel
        viewModel.getAllIncidentLogs().observe(getViewLifecycleOwner(), incidentLogs -> {
            adapter.setIncidentLogs(incidentLogs);
        });

        // Listener for the FloatingActionButton to add a new log
        fab.setOnClickListener(v -> {
            IncidentLogsDetailsFragment addFragment = IncidentLogsDetailsFragment.newInstance(null);
            addFragment.show(getParentFragmentManager(), "IncidentLogDetailsFragment");
        });

        // Set up the click listener for the adapter
        adapter.setOnItemClickListener(incidentLog -> {
            // Here, we pass the incident log to edit
            IncidentLogsDetailsFragment editFragment = IncidentLogsDetailsFragment.newInstance(incidentLog);
            editFragment.show(getParentFragmentManager(), "IncidentLogDetailsFragment");
        });
    }

    // Optionally, a method to reset the FAB when the bottom sheet is dismissed
    public void resetFab() {
        fab.setImageResource(R.drawable.ic_add); // Change FAB icon back to add icon
    }
}