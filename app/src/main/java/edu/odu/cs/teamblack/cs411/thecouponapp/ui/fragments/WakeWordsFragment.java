package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Handler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ai.picovoice.porcupine.Porcupine;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.PorcupineService;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.common.PermissionManager;

public class WakeWordsFragment extends Fragment {

    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE};
    private static final String SHARED_PREFS_NAME = "wake_word_settings";
    private static final String KEY_WAKE_WORD_ENABLED = "wake_word_enabled";
    private static final long DEBOUNCE_DELAY_MS = 500;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private List<Spinner> keywordSpinners;
    private SwitchMaterial enableWakeWordSwitch;
    private PermissionManager permissionManager;
    private SharedPreferences sharedPreferences;
    ArrayList<String> keywords = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionManager = new PermissionManager(this);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wake_words, container, false);

        enableWakeWordSwitch = view.findViewById(R.id.enable_wake_word_control_switch);
        keywordSpinners = Arrays.asList(
                view.findViewById(R.id.keyword_spinner_1),
                view.findViewById(R.id.keyword_spinner_2),
                view.findViewById(R.id.keyword_spinner_3),
                view.findViewById(R.id.keyword_spinner_4)
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getKeywords());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keywordSpinners.forEach(spinner -> {
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(spinnerListener);
            //keywords.add(spinner.getSelectedItem().toString().toUpperCase().replace(" ","_"));
        });

        // Get the saved switch state from SharedPreferences and apply it to the switch
        boolean switchState = sharedPreferences.getBoolean(KEY_WAKE_WORD_ENABLED, false);
        enableWakeWordSwitch.setChecked(switchState);

        // Now set the switch listener
        enableWakeWordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Remove any existing callbacks to debounce
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> {
                sharedPreferences.edit().putBoolean(KEY_WAKE_WORD_ENABLED, isChecked).apply();
                setUIEnabled(isChecked);
                if (isChecked) {
                    if (checkAllPermissionsGranted()) {
                        requestPermissions();
                    } else {
                        startService();
                    }
                } else {
                    stopService();
                }
            }, DEBOUNCE_DELAY_MS);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUIEnabled(sharedPreferences.getBoolean(KEY_WAKE_WORD_ENABLED, false));
    }

    private List<String> getKeywords() {
        return Arrays.stream(Porcupine.BuiltInKeyword.values())
                .map(keyword -> keyword.name().toLowerCase().replace("_", " "))
                .collect(Collectors.toList());
    }

    private void updateServiceAndUIState(boolean enabled) {
        if (enabled) {
            if (checkAllPermissionsGranted()) {
                requestPermissions();
            } else {
                startService();
                setUIEnabled(true);
            }
        } else {
            stopService();
            setUIEnabled(false);
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(requireContext(), PorcupineService.class);
        keywords.add("COMPUTER");
        keywords.add("PORCUPINE");
        keywords.add("BLUEBERRY");
        keywords.add("TERMINATOR");
        serviceIntent.putExtra("keywords",keywords);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(requireContext(), PorcupineService.class);
        requireActivity().stopService(serviceIntent);
    }

    private void setUIEnabled(boolean isEnabled) {
        if (requireView() == null) return;

        int textColor = isEnabled ? getResources().getColor(R.color.text_enabled, null) : getResources().getColor(R.color.text_disabled, null);
        float alpha = isEnabled ? 1.0f : 0.5f;
        keywordSpinners.forEach(spinner -> {
            spinner.setEnabled(isEnabled);
            spinner.setAlpha(alpha);
        });
        updateTextViewsColor(textColor);
    }

    private void updateTextViewsColor(int textColor) {
        int[] titleIDs = {R.id.keyword_title_1, R.id.keyword_title_2, R.id.keyword_title_3, R.id.keyword_title_4};
        int[] subtitleIDs = {R.id.keyword_subtitle_1, R.id.keyword_subtitle_2, R.id.keyword_subtitle_3, R.id.keyword_subtitle_4};
        for (int i = 0; i < titleIDs.length; i++) {
            TextView titleView = requireView().findViewById(titleIDs[i]);
            TextView subtitleView = requireView().findViewById(subtitleIDs[i]);
            if (titleView != null) titleView.setTextColor(textColor);
            if (subtitleView != null) subtitleView.setTextColor(textColor);
        }
    }

    private void requestPermissions() {
        permissionManager.requestPermissions(REQUIRED_PERMISSIONS, permissionsListener);
    }

    private boolean checkAllPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Required")
                .setMessage("Microphone and call permissions are required for this feature. Please enable them in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private final AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final PermissionManager.MultiplePermissionsListener permissionsListener = new PermissionManager.MultiplePermissionsListener() {
        @Override
        public void onAllPermissionsGranted() {
            updateServiceAndUIState(true);
        }

        @Override
        public void onPermissionsDenied(List<String> deniedPermissions) {
            showSnackbarMessage("Permissions denied");
            enableWakeWordSwitch.setChecked(false);
        }

        @Override
        public void onPermissionsDeniedForever(List<String> deniedForeverPermissions) {
            showPermissionDialog();
            enableWakeWordSwitch.setChecked(false);
        }
    };
}

