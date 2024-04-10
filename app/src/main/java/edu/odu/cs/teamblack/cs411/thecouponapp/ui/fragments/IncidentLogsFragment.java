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

import java.util.ArrayList;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters.IncidentLogsAdapter;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.IncidentLogsViewModel;

public class IncidentLogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private IncidentLogsAdapter adapter;
    private IncidentLogsViewModel viewModel;

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
        fabAdd = view.findViewById(R.id.fab_add_incident);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(IncidentLogsViewModel.class);

        // Initialize the adapter with an empty list and set it to the RecyclerView
        adapter = new IncidentLogsAdapter(new ArrayList<>(), incident -> {
            // TODO: Implement the click event for each item
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Observe LiveData from ViewModel
        viewModel.getAllIncidentLogs().observe(getViewLifecycleOwner(), incidentLogs -> {
            // Update the adapter's data
            adapter.setIncidentLogs(incidentLogs);
        });

        // Set up the FAB click listener to add new incident logs
        fabAdd.setOnClickListener(v -> {
            // TODO: Implement the logic to show the create incident log screen
        });
    }
}
