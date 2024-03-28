package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineActivationException;
import ai.picovoice.porcupine.PorcupineActivationLimitException;
import ai.picovoice.porcupine.PorcupineActivationRefusedException;
import ai.picovoice.porcupine.PorcupineActivationThrottledException;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineInvalidArgumentException;
import ai.picovoice.porcupine.PorcupineManager;
import ai.picovoice.porcupine.PorcupineManagerCallback;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class WakeWordsFragment extends Fragment {

    private static final String ACCESS_KEY = "ir/zJzrvkSCpbURXMlpFz1nL5VEHIsNf2snqMTDwXDiEDzc4Cp4zzQ==";

    private PorcupineManager porcupineManager;
    private MediaPlayer notificationPlayer;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // UI elements
    private ToggleButton recordButton;
    private Spinner keywordSpinner;
    private TextView errorMessageText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startPorcupine();
                    } else {
                        showMicrophonePermissionError();
                    }
                });

        // Create notification player
        notificationPlayer = MediaPlayer.create(requireContext(), R.raw.notification);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wake_words, container, false);

        // Find and initialize UI elements
        recordButton = view.findViewById(R.id.record_button);
        keywordSpinner = view.findViewById(R.id.keyword_spinner);
        errorMessageText = view.findViewById(R.id.errorMessage);

        // Configure keyword spinner
        configureKeywordSpinner();

        // Set up record button listener
        recordButton.setOnClickListener(v -> processRecordButtonClick());

        return view;
    }

    // Handles record button click events
    private void processRecordButtonClick() {
        if (recordButton.isChecked()) {
            requestMicrophonePermission();
        } else {
            stopPorcupine();
        }
    }

    // Requests microphone permission
    private void requestMicrophonePermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
    }

    // Starts Porcupine wake word detection
    private void startPorcupine() {
        try {
            String keywordName = getSelectedKeywordName();
            PorcupineManager.Builder builder = new PorcupineManager.Builder()
                    .setAccessKey(ACCESS_KEY)
                    .setSensitivity(0.7f)
                    .setKeyword(getBuiltInKeyword(keywordName)); // Assuming built-in keywords

            porcupineManager = builder.build(requireContext(), porcupineManagerCallback);
            porcupineManager.start();
        } catch (PorcupineException e) {
            handlePorcupineError(e);
        }
    }

    // Gets the selected keyword name from the spinner
    private String getSelectedKeywordName() {
        return keywordSpinner.getSelectedItem().toString();
    }

    // Converts a keyword name to a Porcupine.BuiltInKeyword
    private Porcupine.BuiltInKeyword getBuiltInKeyword(String keywordName) {
        return Porcupine.BuiltInKeyword.valueOf(keywordName.toUpperCase().replace(" ", "_"));
    }

    // Stops Porcupine wake word detection
    private void stopPorcupine() {
        if (porcupineManager != null) {
            try {
                porcupineManager.stop();
                porcupineManager.delete();
            } catch (PorcupineException e) {
                displayError("Failed to stop Porcupine.");
            }
        }
    }

    // Handles Porcupine initialization errors
    private void handlePorcupineError(PorcupineException e) {
        String errorMessage;
        if (e instanceof PorcupineInvalidArgumentException) {
            errorMessage = e.getMessage();
        } else if (e instanceof PorcupineActivationException) {
            errorMessage = "AccessKey activation error";
        } else if (e instanceof PorcupineActivationLimitException) {
            errorMessage = "AccessKey reached its device limit";
        } else if (e instanceof PorcupineActivationRefusedException) {
            errorMessage = "AccessKey refused";
        } else if (e instanceof PorcupineActivationThrottledException) {
            errorMessage = "AccessKey has been throttled";
        } else {
            errorMessage = "Failed to initialize Porcupine: " + e.getMessage();
        }
        showPorcupineError(errorMessage);
    }

    // Displays an error message for Porcupine initialization failure
    private void showPorcupineError(String errorMessage) {
        errorMessageText.setText(errorMessage);
        errorMessageText.setVisibility(View.VISIBLE);
        recordButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
        recordButton.setChecked(false);
        recordButton.setEnabled(false);
    }

    // Displays an error message for missing microphone permission
    private void showMicrophonePermissionError() {
        onPorcupineInitError("Microphone permission is required for this demo");
    }

    private void onPorcupineInitError(final String errorMessage) {
        requireActivity().runOnUiThread(() -> {
            // Display the error message in the TextView
            TextView errorText = requireActivity().findViewById(R.id.errorMessage);
            errorText.setText(errorMessage);
            errorText.setVisibility(View.VISIBLE);

            // Disable the record button
            ToggleButton recordButton = requireActivity().findViewById(R.id.record_button);
            recordButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
            recordButton.setChecked(false);
            recordButton.setEnabled(false);
        });
    }

    // Configures the keyword spinner
    private void configureKeywordSpinner() {
        ArrayList<String> spinnerItems = new ArrayList<>();
        for (Porcupine.BuiltInKeyword keyword : Porcupine.BuiltInKeyword.values()) {
            spinnerItems.add(keyword.toString().toLowerCase().replace("_", " "));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.keyword_spinner_item,
                spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keywordSpinner.setAdapter(adapter);

        keywordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle spinner item selected (e.g., update Porcupine if needed)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected
            }
        });
    }

    // Plays the notification sound and highlights the layout
    private void playNotificationAndHighlightLayout() {
        if (!notificationPlayer.isPlaying()) {
            notificationPlayer.start();
        }

        final RelativeLayout layout = requireActivity().findViewById(R.id.layout);
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));

        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!notificationPlayer.isPlaying()) {
                    notificationPlayer.start();
                }
            }

            @Override
            public void onFinish() {
                layout.setBackgroundColor(Color.TRANSPARENT);
            }
        }.start();
    }

    // Displays an error message as a Toast
    private void displayError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // PorcupineManagerCallback implementation
    private final PorcupineManagerCallback porcupineManagerCallback = new PorcupineManagerCallback() {
        @Override
        public void invoke(int keywordIndex) {
            requireActivity().runOnUiThread(WakeWordsFragment.this::playNotificationAndHighlightLayout);
        }
    };
}