package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Handler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ai.picovoice.porcupine.Porcupine;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.PorcupineService;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.common.PermissionManager;

public class WakeWordsFragment extends Fragment {

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };
    private static final String SHARED_PREFS_NAME = "wake_word_settings";
    private static final String KEY_WAKE_WORD_ENABLED = "wake_word_enabled";
    private static final long DEBOUNCE_DELAY_MS = 500;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private List<AutoCompleteTextView> keywordDropdowns;
    private SwitchMaterial enableWakeWordSwitch;
    private PermissionManager permissionManager;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionManager = new PermissionManager(this);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wake_words_fragment, container, false);

        enableWakeWordSwitch = view.findViewById(R.id.enable_wake_word_control_switch);
        keywordDropdowns = Arrays.asList(
                view.findViewById(R.id.keyword_dropdown_1),
                view.findViewById(R.id.keyword_dropdown_2),
                view.findViewById(R.id.keyword_dropdown_3),
                view.findViewById(R.id.keyword_dropdown_4)
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, getKeywords());
        keywordDropdowns.forEach(dropdown -> {
            dropdown.setAdapter(adapter);
            dropdown.setOnItemClickListener(dropdownListener);
        });

        // Set the default first dropdown to the first item in the list
        keywordDropdowns.forEach(dropdown -> dropdown.setText(adapter.getItem(0), false));

        // Get the saved switch state from SharedPreferences and apply it to the switch
        boolean switchState = sharedPreferences.getBoolean(KEY_WAKE_WORD_ENABLED, false);
        enableWakeWordSwitch.setChecked(switchState);

        // Now set the switch listener
        enableWakeWordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Remove any existing callbacks to debounce
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> {
                sharedPreferences.edit().putBoolean(KEY_WAKE_WORD_ENABLED, isChecked).apply();
                updateServiceAndUIState(isChecked);
            }, DEBOUNCE_DELAY_MS);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateServiceAndUIState(sharedPreferences.getBoolean(KEY_WAKE_WORD_ENABLED, false));
        populateDropdowns();
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
        requireContext().startService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(requireContext(), PorcupineService.class);
        requireActivity().stopService(serviceIntent);
    }

    private void setUIEnabled(boolean isEnabled) {
        if (requireView() == null) return;

        int textColor = isEnabled ? getResources().getColor(R.color.textColorEnabled, null) : getResources().getColor(R.color.textColorDisabled, null);
        float alpha = isEnabled ? 1.0f : 0.5f;

        boolean hasPermissions = !checkAllPermissionsGranted();
        keywordDropdowns.forEach(dropdown -> {
            dropdown.setEnabled(isEnabled && hasPermissions);
            dropdown.setAlpha(alpha);
        });

        updateTextViewsColor(textColor);

        // Enable/disable the TextInputLayout elements
        int[] keywordMenuIDs = {R.id.keyword_menu_1, R.id.keyword_menu_2, R.id.keyword_menu_3, R.id.keyword_menu_4};
        for (int menuID : keywordMenuIDs) {
            TextInputLayout textInputLayout = requireView().findViewById(menuID);
            textInputLayout.setEnabled(isEnabled && hasPermissions);
        }
    }

    private void populateDropdowns() {
        List<String> availableKeywords = new ArrayList<>(getKeywords());
        String[] defaultKeywords = {"porcupine", "bumblebee", "terminator", "blueberry"};

        for (int i = 0; i < keywordDropdowns.size(); i++) {
            AutoCompleteTextView dropdown = keywordDropdowns.get(i);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, availableKeywords);
            dropdown.setAdapter(adapter);
            dropdown.setText(defaultKeywords[i], false);
            availableKeywords.remove(defaultKeywords[i]);
            dropdown.setOnItemClickListener(getDropdownListener(i, availableKeywords));
        }
    }

    private AdapterView.OnItemClickListener getDropdownListener(int index, List<String> availableKeywords) {
        return (parent, view, position, id) -> {
            String selectedKeyword = parent.getItemAtPosition(position).toString();
            availableKeywords.add(keywordDropdowns.get(index).getText().toString());
            availableKeywords.remove(selectedKeyword);
            updateAdapters(index, availableKeywords);
        };
    }

    private void updateAdapters(int excludeIndex, List<String> availableKeywords) {
        for (int i = 0; i < keywordDropdowns.size(); i++) {
            if (i != excludeIndex) {
                AutoCompleteTextView dropdown = keywordDropdowns.get(i);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, availableKeywords);
                dropdown.setAdapter(adapter);
            }
        }
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
            if (requireContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
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

    private final AdapterView.OnItemClickListener dropdownListener = (parent, view, position, id) -> {
        // Handle item selection
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
