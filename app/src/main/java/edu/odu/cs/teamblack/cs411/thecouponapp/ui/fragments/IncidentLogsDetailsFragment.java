package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.IncidentLogsViewModel;


public class IncidentLogsDetailsFragment extends BottomSheetDialogFragment {
    private static final String ARG_INCIDENT_LOG = "incident_log";

    private IncidentLogsViewModel viewModel;
    private IncidentLog incidentLog;
    private TextInputLayout notesInputLayout;
    private TextInputEditText notesInput;
    private Slider severitySlider;
    private Button saveButton;
    private ImageButton closeButton;

    public static IncidentLogsDetailsFragment newInstance(IncidentLog incidentLog) {
        IncidentLogsDetailsFragment fragment = new IncidentLogsDetailsFragment();
        Bundle args = new Bundle();
        if (incidentLog != null) {
            args.putParcelable(ARG_INCIDENT_LOG, incidentLog);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.incident_logs_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(IncidentLogsViewModel.class);

        notesInputLayout = view.findViewById(R.id.notes_input_layout);
        notesInput = view.findViewById(R.id.notes_input);
        severitySlider = view.findViewById(R.id.severity_slider);
        saveButton = view.findViewById(R.id.save_button);
        closeButton = view.findViewById(R.id.close_button);

        if (getArguments() != null && getArguments().containsKey(ARG_INCIDENT_LOG)) {
            incidentLog = getArguments().getParcelable(ARG_INCIDENT_LOG);
            // Populate the views with the properties of incidentLog
        }

        saveButton.setOnClickListener(v -> saveIncidentLog());
        closeButton.setOnClickListener(v -> dismiss());
    }

    private void saveIncidentLog() {
        String notes = notesInput.getText().toString().trim();
        int severityLevel = (int) severitySlider.getValue();

        if (incidentLog == null) {
            incidentLog = new IncidentLog();
        }
        incidentLog.setNotes(notes);

        // Convert severity level from integer to string representation
        String severityString;
        switch (severityLevel) {
            case 0:
                severityString = "Low";
                break;
            case 1:
                severityString = "Medium";
                break;
            case 2:
                severityString = "High";
                break;
            default:
                severityString = "Unknown"; // Default case or consider throwing an exception
                break;
        }
        incidentLog.setSeverity(severityString);

        // Save or update the incident log using the ViewModel
        if (incidentLog.getId() == 0) {
            viewModel.insert(incidentLog);
        } else {
            viewModel.update(incidentLog);
        }

        dismiss(); // Close the bottom sheet or dialog
    }
}