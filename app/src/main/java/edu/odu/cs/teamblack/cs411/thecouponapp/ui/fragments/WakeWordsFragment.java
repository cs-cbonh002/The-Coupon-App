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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ai.picovoice.porcupine.Porcupine;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.PorcupineService;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.common.PermissionManager;

public class WakeWordsFragment extends Fragment {
    private static final int LOCATION_PERMISSION = 44;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    private static final String SHARED_PREFS_NAME = "wake_word_settings";
    private static final String KEY_WAKE_WORD_ENABLED = "wake_word_enabled";
    private static final String WAKE1 = "wake1";
    private static final String WAKE2 = "wake2";
    private static final String WAKE3 = "wake3";
    private static final String WAKE4 = "wake4";
    private static final long DEBOUNCE_DELAY_MS = 500;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private List<AutoCompleteTextView> keywordDropdowns;
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
        View view = inflater.inflate(R.layout.wake_words_fragment, container, false);

        keywords.add(sharedPreferences.getString(WAKE1,"ALEXA"));
        keywords.add(sharedPreferences.getString(WAKE2,"BUMBLEBEE"));
        keywords.add(sharedPreferences.getString(WAKE3,"TERMINATOR"));
        keywords.add(sharedPreferences.getString(WAKE4,"BLUEBERRY"));

        enableWakeWordSwitch = view.findViewById(R.id.enable_wake_word_control_switch);
        keywordDropdowns = Arrays.asList(
                view.findViewById(R.id.keyword_dropdown_1),
                view.findViewById(R.id.keyword_dropdown_2),
                view.findViewById(R.id.keyword_dropdown_3),
                view.findViewById(R.id.keyword_dropdown_4)
        );

        List<String> words = getKeywords();
        words.removeAll(keywords);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, words);
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
        List<String> getKey = Arrays.stream(Porcupine.BuiltInKeyword.values())
                .map(keyword -> keyword.name())
                .collect(Collectors.toList());
        return getKey;
    }

    private void updateServiceAndUIState(boolean enabled) {
        if (enabled) {
            if (checkAllPermissionsGranted()) {
                requestPermissions();
                requestGpsPermissions();
            } else {
                setUIEnabled(false);
                startService();
            }
        } else {
            stopService();
            setUIEnabled(true);
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(requireContext(), PorcupineService.class);
        serviceIntent.putExtra("keywords",keywords);
        requireContext().startForegroundService(serviceIntent);
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
            availableKeywords.removeAll(keywords);

        for (int i = 0; i < keywordDropdowns.size(); i++) {
            AutoCompleteTextView dropdown = keywordDropdowns.get(i);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, availableKeywords);
            dropdown.setAdapter(adapter);
            dropdown.setText(keywords.get(i), false);
            dropdown.setOnItemClickListener(getDropdownListener(i));
        }
    }

    private AdapterView.OnItemClickListener getDropdownListener(int index) {
        return (parent, view, position, id) -> {
            String selectedKeyword = parent.getItemAtPosition(position).toString();
            updateAdapters(index);
            keywords.set(index,selectedKeyword);
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> {
                switch (index) {
                    case 0:
                        sharedPreferences.edit().putString(WAKE1,selectedKeyword).apply();
                        break;
                    case 1:
                        sharedPreferences.edit().putString(WAKE2,selectedKeyword).apply();
                        break;
                    case 2:
                        sharedPreferences.edit().putString(WAKE3,selectedKeyword).apply();
                        break;
                    case 3:
                        sharedPreferences.edit().putString(WAKE4,selectedKeyword).apply();
                        break;
                }
            }, DEBOUNCE_DELAY_MS);
            populateDropdowns();
        };
    }

    private void updateAdapters(int excludeIndex) {
        List<String> availableKeywords = new ArrayList<>(getKeywords());
        availableKeywords.removeAll(keywords);
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
        for (int i = 0; i < titleIDs.length; i++) {
            TextView titleView = requireView().findViewById(titleIDs[i]);
            if (titleView != null) titleView.setTextColor(textColor);
        }
    }

    private void requestPermissions() {
        permissionManager.requestPermissions(REQUIRED_PERMISSIONS, permissionsListener);
    }

    private boolean checkAllPermissionsGranted() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            return true;
        }
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
        populateDropdowns();
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

    private void requestGpsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to record GPS in the background.\nPlease select \"Allow all the time\" in settings")
                    .setPositiveButton("ok", (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(getActivity(), new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION))
                    .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create().show();

        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
    }
}
