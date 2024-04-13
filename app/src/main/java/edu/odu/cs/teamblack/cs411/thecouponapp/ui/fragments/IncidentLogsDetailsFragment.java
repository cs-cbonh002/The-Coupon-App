package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import java.text.ParseException;
import java.util.Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import android.widget.LinearLayout; // Import for setInputMode
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.IncidentLogsViewModel;

// Required imports for the date and time pickers
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// The rest of your imports stay the same...

public class IncidentLogsDetailsFragment extends BottomSheetDialogFragment {
    private static final String ARG_INCIDENT_LOG = "incident_log";

    private IncidentLogsViewModel viewModel;
    private IncidentLog incidentLog;
    private TextInputEditText dateInput; // Replaces DatePicker
    private TextInputEditText timeInput; // Replaces TimePicker
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

        initializeViews(view);
        setupListeners();

        if (getArguments() != null && getArguments().containsKey(ARG_INCIDENT_LOG)) {
            incidentLog = getArguments().getParcelable(ARG_INCIDENT_LOG, IncidentLog.class);
            if (incidentLog != null) {
                populateViews();
            }
        }
    }

    private void initializeViews(View view) {
        notesInput = view.findViewById(R.id.notes_input);
        severitySlider = view.findViewById(R.id.severity_slider);
        saveButton = view.findViewById(R.id.save_button);
        closeButton = view.findViewById(R.id.close_button);
        dateInput = view.findViewById(R.id.date_input); // Initialized for date input
        timeInput = view.findViewById(R.id.time_input); // Initialized for time input
    }

    private void populateViews() {
        notesInput.setText(incidentLog.getNotes());
        severitySlider.setValue(getSeverityValue(incidentLog.getSeverity()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateInput.setText(dateFormat.format(incidentLog.getIncidentDate()));
        timeInput.setText(timeFormat.format(incidentLog.getIncidentDate()));
    }

    private void setupListeners() {
        dateInput.setOnClickListener(v -> showDatePicker());
        timeInput.setOnClickListener(v -> showTimePicker());
        saveButton.setOnClickListener(v -> saveIncidentLog());
        closeButton.setOnClickListener(v -> dismiss());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
        datePicker.show(getParentFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateInput.setText(dateFormat.format(new Date(selection)));
        });
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // Use a 12-hour clock
                .build();
        timePicker.show(getParentFragmentManager(), timePicker.toString());
        timePicker.addOnPositiveButtonClickListener(dialog -> {
            // Format the time and include AM/PM
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                    timePicker.getHour(), timePicker.getMinute(),
                    timePicker.getHour() < 12 ? "AM" : "PM");
            timeInput.setText(formattedTime);
        });
    }


    private int getSeverityValue(String severity) {
        switch (severity) {
            case "Low":
                return 0;
            case "Medium":
                return 1;
            case "High":
                return 2;
            default:
                return 0; // Default to low severity
        }
    }

    private void saveIncidentLog() {
        String notes = notesInput.getText().toString().trim();
        int severityLevel = (int) severitySlider.getValue();

        if (incidentLog == null) {
            incidentLog = new IncidentLog();
        }
        incidentLog.setNotes(notes);

        // Parse the date and time including AM/PM
        String dateString = dateInput.getText().toString();
        String timeString = timeInput.getText().toString(); // This should be in "hh:mm a" format

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(dateString + " " + timeString));
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing error
        }

        incidentLog.setIncidentDate(calendar.getTime());

        // Convert severity level from integer to string representation
        String severityString = getSeverityString(severityLevel);
        incidentLog.setSeverity(severityString);

        // Save or update the incident log using the ViewModel
        if (incidentLog.getId() == 0) {
            viewModel.insert(incidentLog);
        } else {
            viewModel.update(incidentLog);
        }

        dismiss(); // Close the bottom sheet or dialog
    }

    private IncidentLog createOrUpdateIncidentLog(String notes, String severity, Date incidentDate) {
        if (incidentLog == null) {
            incidentLog = new IncidentLog();
        }
        incidentLog.setNotes(notes);
        incidentLog.setSeverity(severity);
        incidentLog.setIncidentDate(incidentDate);
        return incidentLog;
    }

    private void persistIncidentLog() {
        if (incidentLog.getId() == 0) {
            viewModel.insert(incidentLog);
        } else {
            viewModel.update(incidentLog);
        }
    }

    private String getSeverityString(int severityLevel) {
        switch (severityLevel) {
            case 0:
                return "Low";
            case 1:
                return "Medium";
            case 2:
                return "High";
            default:
                return "Unknown"; // Consider an exception or a default case
        }
    }
}
