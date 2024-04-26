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
    private FloatingActionButton add_log_button;
    private IncidentLogsAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.incident_logs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_incident_logs);
        add_log_button = view.findViewById(R.id.add_log_button);

        viewModel = new ViewModelProvider(this).get(IncidentLogsViewModel.class);

        adapter = new IncidentLogsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllIncidentLogs().observe(getViewLifecycleOwner(), incidentLogs -> {
            adapter.setIncidentLogs(incidentLogs);
        });

        add_log_button.setOnClickListener(v -> {
            IncidentLogsDetailsFragment addFragment = IncidentLogsDetailsFragment.newInstance(null);
            addFragment.show(getParentFragmentManager(), "IncidentLogDetailsFragment");
        });

        adapter.setOnItemClickListener(incidentLog -> {
            IncidentLogsDetailsFragment editFragment = IncidentLogsDetailsFragment.newInstance(incidentLog);
            editFragment.show(getParentFragmentManager(), "IncidentLogDetailsFragment");
        });
    }
}