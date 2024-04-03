/*
    Copyright 2021 Picovoice Inc.

    You may not use this file except in compliance with the license. A copy of the license is
    located in the "LICENSE" file accompanying this source.

    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
    express or implied. See the License for the specific language governing permissions and
    limitations under the License.

*/

package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ai.picovoice.porcupine.Porcupine;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.PorcupineService;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.common.PermissionManager;

public class WakeWordsFragment extends Fragment {

    //permissions
    private PermissionManager permissionManager;

    // UI elements
    private ToggleButton recordButton;
    private Spinner keywordSpinner, keywordSpinner2;
    private TextView errorMessageText;
    private ServiceBroadcastReceiver receiver;

    public void startService() {
        Intent serviceIntent = new Intent(getContext(), PorcupineService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            getActivity().startForegroundService(serviceIntent);
        } else {
            getActivity().startService(serviceIntent);
        }
    }

    private void stopService() {
        Intent serviceIntent = new Intent(getContext(), PorcupineService.class);
        getActivity().stopService(serviceIntent);
    }

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
        keywordSpinner2.setAdapter(adapter);

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
        keywordSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void setupUI(View view) {
        recordButton = view.findViewById(R.id.record_button);
        keywordSpinner = view.findViewById(R.id.keyword_spinner);
        keywordSpinner2 = view.findViewById(R.id.keyword_spinner2);
        errorMessageText = view.findViewById(R.id.errorMessage);

        recordButton.setOnClickListener(v -> {
            if (recordButton.isChecked()) {
                permissionManager.requestPermission(Manifest.permission.RECORD_AUDIO, new PermissionManager.PermissionResultListener() {
                    @Override
                    public void onPermissionGranted(String permission) {
                        startService();
                    }

                    @Override
                    public void onPermissionDenied(String permission) {
                        showMicrophonePermissionError();
                    }

                    @Override
                    public void onPermissionDeniedForever(String permission) {
                        // Handle "Don't ask again" case if necessary
                        showPermissionDialog();
                    }
                });
            } else {
                stopService();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wake_words, container, false);
        setupUI(view);
        configureKeywordSpinner();
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new ServiceBroadcastReceiver();
        permissionManager = new PermissionManager(this);
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission required")
                .setMessage("This feature requires microphone permission. Please enable it in app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getContext().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("PorcupineInitError");
        getActivity().registerReceiver(receiver, filter, null, null, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
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

    public class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onPorcupineInitError(intent.getStringExtra("errorMessage"));
        }
    }
}
