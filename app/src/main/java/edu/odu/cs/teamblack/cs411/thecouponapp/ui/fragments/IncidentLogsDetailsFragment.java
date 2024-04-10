package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

public class IncidentLogsDetailsFragment extends BottomSheetDialogFragment {
    private static final String ARG_INCIDENT_LOG = "incident_log";
    private IncidentLog incidentLog;

    private TextInputLayout notesInputLayout;
    private TextInputEditText notesInput;
    private TextInputLayout severityInputLayout;
    private TextInputEditText severityInput;
    private Button saveButton;
    private Button deleteButton;

    public static IncidentLogsDetailsFragment newInstance(@Nullable IncidentLog incidentLog) {
        IncidentLogsDetailsFragment fragment = new IncidentLogsDetailsFragment();
        Bundle args = new Bundle();
        if (incidentLog != null) {
            // Assuming IncidentLog is Parcelable for performance
            args.putParcelable(ARG_INCIDENT_LOG, incidentLog);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_INCIDENT_LOG)) {
            incidentLog = getArguments().getParcelable(ARG_INCIDENT_LOG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incident_logs_details, container, false);
        notesInputLayout = view.findViewById(R.id.notes_input_layout);
        notesInput = view.findViewById(R.id.notes_input);
        severityInputLayout = view.findViewById(R.id.severity_input_layout);
        severityInput = view.findViewById(R.id.severity_input);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);

        setupViewBasedOnMode();
        return view;
    }

    private void setupViewBasedOnMode() {
        if (incidentLog == null) {
            // Setup views for adding a new incident log
            deleteButton.setVisibility(View.GONE);
        } else {
            // Setup views for viewing or editing an existing incident log
            notesInput.setText(incidentLog.getNotes());
            severityInput.setText(incidentLog.getSeverity());

            if (incidentLog.isCreatedByUser()) {
                // Allow editing for user-created incident logs
                notesInput.setEnabled(true);
                severityInput.setEnabled(true);
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                // Disable editing for system-generated incident logs
                notesInput.setEnabled(false);
                severityInput.setEnabled(false);
                deleteButton.setVisibility(View.GONE);
            }
        }

        saveButton.setOnClickListener(v -> {
            String notes = notesInput.getText().toString().trim();
            String severity = severityInput.getText().toString().trim();

            if (incidentLog == null) {
                // Create a new incident log
                IncidentLog newIncidentLog = new IncidentLog();
                newIncidentLog.setNotes(notes);
                newIncidentLog.setSeverity(severity);
                // Set other fields like timestamp, location, user badge, etc.
                // Save the new incident log
            } else {
                // Update the existing incident log
                incidentLog.setNotes(notes);
                incidentLog.setSeverity(severity);
                // Save the updated incident log
            }

            dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            // Delete the incident log
            dismiss();
        });
    }
}