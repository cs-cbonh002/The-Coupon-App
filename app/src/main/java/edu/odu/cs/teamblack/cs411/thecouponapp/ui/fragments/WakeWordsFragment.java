package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import ai.picovoice.porcupine.Porcupine;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.PorcupineService;
import edu.odu.cs.teamblack.cs411.thecouponapp.managers.PermissionsManager;

public class WakeWordsFragment extends Fragment implements PorcupineService.WakeWordDetectionListener {
    private static final String WAKE_WORD_PREFS = "WakeWordsPrefs";
    private static final int NUM_WAKE_WORDS = 4;
    private static final String[] DEFAULT_WAKE_WORDS = {"PORCUPINE", "BLUEBERRY", "TERMINATOR", "JARVIS"};
    private static final Set<String> BUILT_IN_KEYWORDS;
    private PorcupineService porcupineService;
    private SharedPreferences sharedPreferences;
    private boolean isBound = false;
    private List<AutoCompleteTextView> wakeWordDropdowns;
    private SwitchMaterial enableWakeWordSwitch;
    private PermissionsManager permissionsManager;
    private ServiceConnection serviceConnection;

    private final Map<Integer, String> selectedWakeWords = new HashMap<>();
    private Set<String> availableWakeWords = new HashSet<>();

    static {
        BUILT_IN_KEYWORDS = Arrays.stream(Porcupine.BuiltInKeyword.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireContext().getSharedPreferences(WAKE_WORD_PREFS, Context.MODE_PRIVATE);
        permissionsManager = new PermissionsManager(this);
        initializeServiceConnection();
        initializeWakeWords();
    }

    private void initializeServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                porcupineService = ((PorcupineService.LocalBinder) service).getService();
                porcupineService.setWakeWordDetectionListener(WakeWordsFragment.this);
                isBound = true;
                boolean isDetectionActive = porcupineService.isWakeWordDetectionActive();
                enableWakeWordSwitch.setChecked(isDetectionActive);
                setUIEnabled(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isBound = false;
                setUIEnabled(false);
            }
        };
    }

    private void initializeWakeWords() {
        loadDefaultWakeWords();
        loadAvailableWakeWords();
        loadSelectedWakeWordsFromPreferences();
    }

    private void loadDefaultWakeWords() {
        boolean wakeWordsSet = sharedPreferences.getBoolean("wake_words_set", false);
        if (!wakeWordsSet) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (int i = 0; i < NUM_WAKE_WORDS; i++) {
                editor.putString("wake_word_" + i, DEFAULT_WAKE_WORDS[i]);
            }
            editor.putBoolean("wake_words_set", true);
            editor.apply();
        }
    }

    private void loadAvailableWakeWords() {
        availableWakeWords = new HashSet<>(BUILT_IN_KEYWORDS);
    }

    private void loadSelectedWakeWordsFromPreferences() {
        for (int i = 0; i < NUM_WAKE_WORDS; i++) {
            String wakeWord = sharedPreferences.getString("wake_word_" + i, DEFAULT_WAKE_WORDS[i]);
            selectedWakeWords.put(i, wakeWord);
            availableWakeWords.remove(wakeWord);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wake_words_fragment, container, false);
        setupViews(view);
        restoreSwitchState();
        return view;
    }

    private void setupViews(View view) {
        enableWakeWordSwitch = view.findViewById(R.id.enable_wake_word_control_switch);
        wakeWordDropdowns = Arrays.asList(
                view.findViewById(R.id.keyword_dropdown_1),
                view.findViewById(R.id.keyword_dropdown_2),
                view.findViewById(R.id.keyword_dropdown_3),
                view.findViewById(R.id.keyword_dropdown_4)
        );
        setupDropdownAdapters();
        setupSwitchListener();
    }

    private void setupSwitchListener() {
        enableWakeWordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleWakeWordDetection(isChecked));
    }

    private void setupDropdownAdapters() {
        for (int i = 0; i < wakeWordDropdowns.size(); i++) {
            updateSingleDropdownAdapter(i);
        }
    }

    private void updateSingleDropdownAdapter(int index) {
        AutoCompleteTextView dropdown = wakeWordDropdowns.get(index);
        String currentWakeWord = selectedWakeWords.getOrDefault(index, DEFAULT_WAKE_WORDS[index]);
        List<String> adapterWakeWords = new ArrayList<>(availableWakeWords);
        adapterWakeWords.add(currentWakeWord);  // Add the current selection to the top or ensure it's included
        Collections.sort(adapterWakeWords);     // Sort the wake words for better UI experience

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, adapterWakeWords);
        dropdown.setAdapter(adapter);
        dropdown.setText(currentWakeWord, false);  // Set the currently selected word in the dropdown

        // Set the listener for when an item is selected
        dropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedWord = adapter.getItem(position);
            if (!selectedWord.equals(selectedWakeWords.get(index))) {
                selectedWakeWords.put(index, selectedWord);  // Update the selected wake words
                updateServiceWithSelectedWakeWords();         // Update the service with the new selection
            }
        });
    }

    private void updateServiceWithSelectedWakeWords() {
        // Save the selected wake words to the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<Integer, String> entry : selectedWakeWords.entrySet()) {
            editor.putString("wake_word_" + entry.getKey(), entry.getValue());
        }
        editor.apply();

        // Restart the PorcupineService with the new wake words
        stopWakeWordDetection(); // This will unbind and stop the service
        startWakeWordService();  // This will start and bind the service with new wake words
    }

    private void toggleWakeWordDetection(boolean enable) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("switch_state", enable);
        editor.apply();
        if (enable) {
            checkPermissionsAndStart();
        } else {
            stopWakeWordDetection();
            setUIEnabled(false);
        }
    }

    private void checkPermissionsAndStart() {
        String[] requiredPermissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SEND_SMS
        };
        permissionsManager.requestPermissions(requiredPermissions, new PermissionsManager.MultiplePermissionsListener() {
            @Override
            public void onAllPermissionsGranted() {
                startWakeWordService();
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                enableWakeWordSwitch.setChecked(false);
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, "Permission Denied", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionsDeniedForever(List<String> deniedForeverPermissions) {
                showPermissionDialog();
            }
        });
    }

    private void restoreSwitchState() {
        boolean switchState = sharedPreferences.getBoolean("switch_state", false);
        enableWakeWordSwitch.setChecked(switchState);
        toggleWakeWordDetection(switchState);
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage("Permission is needed to access the microphone. Please enable it in settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", requireContext().getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void startWakeWordService() {
        stopWakeWordDetection();
        Intent serviceIntent = new Intent(getContext(), PorcupineService.class);
        Bundle wakeWordsBundle = new Bundle();
        for (Map.Entry<Integer, String> entry : selectedWakeWords.entrySet()) {
            wakeWordsBundle.putString(entry.getKey().toString(), entry.getValue());
        }
        serviceIntent.putExtra("selected_wake_words_bundle", wakeWordsBundle);
        requireContext().startService(serviceIntent);
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopWakeWordDetection() {
        if (porcupineService != null && isBound) {
            try {
                porcupineService.stopWakeWordDetection();
                requireContext().unbindService(serviceConnection);
                isBound = false;
            } catch (Exception e) {
                Log.e("WakeWordsFragment", "Failed to stop wake word detection", e);
            }
        }
        Intent serviceIntent = new Intent(getContext(), PorcupineService.class);
        requireContext().stopService(serviceIntent);
    }

    private void setUIEnabled(boolean isEnabled) {
        for (AutoCompleteTextView dropdown : wakeWordDropdowns) {
            dropdown.setEnabled(isEnabled);
            dropdown.setAlpha(isEnabled ? 1.0f : 0.5f);
        }
    }

    @Override
    public void onDestroy() {
        if (isBound) {
            requireContext().unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }


    @Override
    public void onWakeWordDetected(int keywordIndex) {
        Log.d("WakeWordsFragment", "Wake word detected: " + keywordIndex);
    }

    @Override
    public void onError(Exception e) {
        Log.e("WakeWordsFragment", "Error from PorcupineService", e);
    }
}
