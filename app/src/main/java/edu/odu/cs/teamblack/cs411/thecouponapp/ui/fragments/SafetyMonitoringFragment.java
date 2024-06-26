//https://www.youtube.com/watch?v=aL528F-AqsE
package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.AudioClassifierService;

public class SafetyMonitoringFragment extends Fragment {

    final String TAG = "AudioClassifierFragment";
    // TensorFlow Lite

    //permissions
    int permissionsCount = 0;
    ArrayList<String> permissionsList;
    final String[] permissionsStr = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SEND_SMS
    };
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    //UI
    private ToggleButton toggleButton;
    private TextView channelTextView;
    private TextView outText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //receiver = new WakeWordsFragment.ServiceBroadcastReceiver();
        initPermission();

        //ask permissions
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_classifier_fragment, container, false);
        toggleButton = view.findViewById(R.id.audioClassifierToggleButton);
        channelTextView = view.findViewById(R.id.channelsTextView);
        outText = view.findViewById(R.id.audioClassifierOutText);

        toggleButton.setOnClickListener(v ->  {
            if (toggleButton.isChecked()) {
                if (hasPermission(requireContext(),permissionsStr)) {
                    startService();
                } else {
                    initPermission();
                }
            } else {
                stopService();
            }
        });
        return view;
    }

    /////////// TensorFlow
    private void startService() {
        Intent serviceIntent = new Intent(requireContext(), AudioClassifierService.class);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(requireContext(), AudioClassifierService.class);
        requireActivity().stopService(serviceIntent);
    }

    ///////////////////////////// Permissions
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

    private void initPermission() {
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
                        }  //All permissions granted. Do your stuff 🤞
                    }
                });
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean hasPermission(Context context, String[] permissionStr) {
        for (String s : permissionStr) {
            if (ContextCompat.checkSelfPermission(context, s) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    AlertDialog alertDialog;
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Permission required")
                .setMessage("Some permissions are need to be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> dialog.dismiss());
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }
}