package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.IncidentLogsViewModel;

public class IncidentLogsDetailsFragment extends BottomSheetDialogFragment {
    private static final String ARG_INCIDENT_LOG = "incident_log";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "hh:mm a";
    private static final String[] SEVERITY_LEVELS = {"Low", "Medium", "High"};

    private IncidentLogsViewModel viewModel;
    private IncidentLog incidentLog;
    private TextInputEditText dateInput;
    private TextInputEditText timeInput;
    private TextInputEditText notesInput;
    private Slider severitySlider;
    private Button saveButton;
    private ImageButton closeButton;
    private TextInputEditText transcriptionInput;
    private ImageButton  audioButton;
    private ProgressBar mediaProgressBar;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    public static IncidentLogsDetailsFragment newInstance(IncidentLog incidentLog) {
        IncidentLogsDetailsFragment fragment = new IncidentLogsDetailsFragment();
        Bundle args = new Bundle();
        if (incidentLog != null) {
            args.putParcelable(ARG_INCIDENT_LOG, incidentLog);
        } else {
            // If incidentLog is null, indicating a new log is being added,
            // set a flag to hide transcription and playback controls
            args.putBoolean("isNewLog", true);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.incident_logs_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(IncidentLogsViewModel.class);

        initializeViews(view);
        setupListeners();

        if (getArguments() != null) {
            if (getArguments().getBoolean("isNewLog", false)) {
                // Hide the transcription and playback controls
                transcriptionInput.setVisibility(View.GONE);
                audioButton.setVisibility(View.GONE);
                mediaProgressBar.setVisibility(View.GONE);
            } else if (getArguments().containsKey(ARG_INCIDENT_LOG)) {
                incidentLog = getArguments().getParcelable(ARG_INCIDENT_LOG);
                if (incidentLog != null) {
                    populateViews();
                }
            }
        }
    }

    private void initializeViews(View view) {
        notesInput = view.findViewById(R.id.notes_input);
        severitySlider = view.findViewById(R.id.severity_slider);
        saveButton = view.findViewById(R.id.save_button);
        closeButton = view.findViewById(R.id.close_button);
        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        transcriptionInput = view.findViewById(R.id.transcription_input);
        audioButton = view.findViewById(R.id.audio_button);
        mediaProgressBar = view.findViewById(R.id.media_progress_bar);
    }

    private void populateViews() {
        if (incidentLog != null) {
            notesInput.setText(incidentLog.getNotes());
            severitySlider.setValue(getSeverityValue(incidentLog.getSeverity()));

            Instant incidentInstant = incidentLog.getIncidentDate().toInstant();
            LocalDateTime dateTime = incidentInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            dateInput.setText(dateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            timeInput.setText(dateTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));

            if (incidentLog.isCreatedByUser()) {
                // Hide the transcription and playback controls for user-created logs
                transcriptionInput.setVisibility(View.GONE);
                audioButton.setVisibility(View.GONE);
                mediaProgressBar.setVisibility(View.GONE);
            } else {
                // Only show the transcription and playback controls for system-created logs
                transcriptionInput.setVisibility(View.VISIBLE);
                audioButton.setVisibility(View.VISIBLE); // Show only if there is an audio file
                mediaProgressBar.setVisibility(View.GONE);   // Initially hidden, shown during playback

                transcriptionInput.setText(incidentLog.getTranscription());
                // Configure the play button and progress bar
                Log.d("IncidentLogsDetails", "Audio file path: " + incidentLog.getAudioFilePath());
                configureMediaPlayback(incidentLog.getAudioFilePath());
            }
        }
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
            // Convert the selection to the desired time zone (UTC)
            ZonedDateTime selectedDateTime = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault());
            ZonedDateTime utcDateTime = selectedDateTime.withZoneSameInstant(ZoneOffset.UTC);

            // Get the local date in the desired time zone (UTC)
            LocalDate localDate = utcDateTime.toLocalDate();

            // Format the local date and set it to the date input field
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.getDefault());
            dateInput.setText(localDate.format(formatter));
        });
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .build();
        timePicker.show(getParentFragmentManager(), timePicker.toString());
        timePicker.addOnPositiveButtonClickListener(dialog -> {
            LocalTime time = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
            timeInput.setText(time.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
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
                return 0;
        }
    }

    private String getSeverityString(int severityLevel) {
        if (severityLevel >= 0 && severityLevel < SEVERITY_LEVELS.length) {
            return SEVERITY_LEVELS[severityLevel];
        } else {
            return SEVERITY_LEVELS[0];
        }
    }

    private void saveIncidentLog() {
        String notes = notesInput.getText().toString().trim();
        String severityString = getSeverityString((int) severitySlider.getValue());
        String dateString = dateInput.getText().toString();
        String timeString = timeInput.getText().toString();

        if (incidentLog == null) {
            incidentLog = new IncidentLog();
        }

        incidentLog.setNotes(notes);
        incidentLog.setSeverity(severityString);

        if (!dateString.isEmpty() && !timeString.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT));
                LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(TIME_FORMAT));
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                Instant utcInstant = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toInstant();
                incidentLog.setIncidentDate(Date.from(utcInstant));
            } catch (DateTimeParseException e) {
                Log.e("IncidentLogsDetails", "Error parsing date/time: ", e);
            }
        } else {
            Log.e("IncidentLogsDetails", "Date or time field is empty");
        }

        if (incidentLog.getId() == 0) {
            viewModel.insert(incidentLog);
        } else {
            viewModel.update(incidentLog);
        }
        dismiss();
    }



    private void configureMediaPlayback(String audioFilePath) {
        audioButton.setVisibility(audioFilePath != null && !audioFilePath.isEmpty() ? View.VISIBLE : View.GONE);
        mediaProgressBar.setVisibility(View.GONE);

        audioButton.setOnClickListener(v -> {
            if (isPlaying) {
                stopAudioPlayback();
            } else {
                startAudioPlayback(audioFilePath);
            }
        });
    }

    private void startAudioPlayback(String audioFilePath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Update UI for playing state
            isPlaying = true;
            audioButton.setImageResource(R.drawable.ic_stop);

            mediaProgressBar.setMax(mediaPlayer.getDuration() / 1000);
            progressHandler.post(progressRunnable);

            mediaPlayer.setOnCompletionListener(mp -> stopAudioPlayback());
        } catch (IOException e) {
            Log.e("IncidentLogsDetailsFragment", "Could not play audio", e);
            // Handle error
        }
    }


    private void stopAudioPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Update UI for not playing state
        mediaProgressBar.setProgress(0);
        isPlaying = false;
        audioButton.setImageResource(R.drawable.ic_play);
        progressHandler.removeCallbacks(progressRunnable);
    }


    @Override
    public void onDestroyView() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        progressHandler.removeCallbacks(progressRunnable);
        super.onDestroyView();
    }

    private Handler progressHandler = new Handler();
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                mediaProgressBar.setProgress(mCurrentPosition);
                // Schedule the runnable to run again after a delay
                progressHandler.postDelayed(this, 1000);
            }
        }
    };
}


