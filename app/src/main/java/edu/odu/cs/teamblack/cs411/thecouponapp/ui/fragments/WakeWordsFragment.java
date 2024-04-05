package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

/*
    Copyright 2021 Picovoice Inc.

    You may not use this file except in compliance with the license. A copy of the license is
    located in the "LICENSE" file accompanying this source.

    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language governing permissions and
limitations under the License.
*/

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ai.picovoice.porcupine.Porcupine;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.PorcupineService;

public class WakeWordsFragment extends Fragment {

    private ServiceBroadcastReceiver receiver;

    //permissions
    int permissionsCount = 0;
    ArrayList<String> permissionsList;
    final String[] permissionsStr = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS
    };
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    // UI elements
    private ToggleButton recordButton;
    private Spinner keywordSpinner;
    private Spinner keywordSpinner2;
    private TextView errorMessageText;


    private boolean hasRecordPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
    }

    public void startService() {
        Intent serviceIntent = new Intent(getContext(), PorcupineService.class);
        requireActivity().startForegroundService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(getContext(), PorcupineService.class);
        requireActivity().stopService(serviceIntent);
    }

    private void configureKeywordSpinner() {
        ArrayList<String> spinnerItems = new ArrayList<>();
        for (Porcupine.BuiltInKeyword keyword : Porcupine.BuiltInKeyword.values()) {
            spinnerItems.add(keyword.toString().toLowerCase().replace("_", " "));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wake_words, container, false);

        // Find and initialize UI elements
        recordButton = view.findViewById(R.id.record_button);
        keywordSpinner = view.findViewById(R.id.keyword_spinner);
        keywordSpinner2 = view.findViewById(R.id.keyword_spinner2);
        errorMessageText = view.findViewById(R.id.errorMessage);

        // Configure keyword spinner
        configureKeywordSpinner();


        recordButton.setOnClickListener(v ->

        {
            if (recordButton.isChecked()) {
                if (hasRecordPermission()) {
                    startService();
                } else {
                    requestRecordPermission();
                }
            } else {
                stopService();
            }
        });

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new ServiceBroadcastReceiver();

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        ArrayList<Boolean> list = new ArrayList<>(result.values());
                        permissionsList = new ArrayList<>();
                        permissionsCount = 0;
                        for (int i = 0; i < list.size(); i++) {
                            if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                permissionsList.add(permissionsStr[i]);
                            } else if (!hasPermission(getActivity(), permissionsStr[i])) {
                                permissionsCount++;
                            }
                        }
                        if (!permissionsList.isEmpty()) {
                            //Some permissions are denied and can be asked again.
                            askForPermissions(permissionsList);
                        } else if (permissionsCount > 0) {
                            //Show alert dialog
                            showPermissionDialog();
                        } else {
                            //All permissions granted. Do your stuff 🤞
                        }
                    }
                });

        //ask permissions
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);


    }
    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }
    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            requestPermissionLauncher.launch(newPermissionStr);
        } else {
        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }

    AlertDialog alertDialog;
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Permission required")
                .setMessage("Some permissions are need to be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    dialog.dismiss();
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(receiver, new IntentFilter("PorcupineInitError"), Context.RECEIVER_NOT_EXPORTED);
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
