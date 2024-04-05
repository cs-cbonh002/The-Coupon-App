package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;

public class SettingsFragment extends Fragment {

    private static final String CHANNEL_ID = "edu.odu.cs.teamblack.cs411.thecouponapp.settings";

    private SwitchMaterial switchLocation, switchMicrophone, switchNotifications;
    private ActivityResultLauncher<String> requestPermissionLauncher, requestNotificationPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePermissionLaunchers();
        createNotificationChannel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        initializeUI(view);
        return view;
    }

    private void initializePermissionLaunchers() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        requestNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                showNotification("Notification Permission Granted", "You can now receive notifications.");
            } else {
                Toast.makeText(getContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeUI(View rootView) {
        // Obtain the featuresGroup LinearLayout by its ID
        View featuresGroup = rootView.findViewById(R.id.featuresGroup);

        SwitchMaterial switchNotifications = featuresGroup.findViewById(R.id.switchNotifications);
        RelativeLayout wakeWordsSetting = featuresGroup.findViewById(R.id.wakeWordsSetting);
        RelativeLayout safetyMonitoringSetting = featuresGroup.findViewById(R.id.safetyMonitoringSetting);
        RelativeLayout gpsSpoofingSetting = featuresGroup.findViewById(R.id.gpsSpoofingSetting);

        switchNotifications.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                showNotification("Notifications Enabled", "Notifications will be shown.");
            }
        });

        wakeWordsSetting.setOnClickListener(v -> navigateToWakeWordsFragment());
        safetyMonitoringSetting.setOnClickListener(v -> navigateToSafetyMonitoringFragment());
        gpsSpoofingSetting.setOnClickListener(v -> navigateToGpsSpoofingFragment());
    }

    private void navigateToWakeWordsFragment() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateTo(new WakeWordsFragment(), true);
        }
    }

    private void navigateToSafetyMonitoringFragment() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateTo(new SafetyMonitoringFragment(), true);
        }
    }

    private void navigateToGpsSpoofingFragment() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateTo(new GpsSpoofingFragment(), true);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String title, String content) {
        // Ensure we are attached to a context
        Context context = getContext();
        Log Log;
        if (context == null) {
            android.util.Log.e("Settings", "Context is null in showNotification");
            return; // Context is not available, exit early
        }

        // Explicit check for Android version and permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                android.util.Log.e("Settings", "POST_NOTIFICATIONS permission not granted");
                Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT).show();
                return; // Permission not granted for POST_NOTIFICATIONS, exit early
            }
        }

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications) // Make sure this resource exists
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());
            android.util.Log.d("ProfileAndSettings", "Notification shown successfully.");
        } catch (Exception e) {
            // Catching a generic exception is a last resort and generally not recommended
            // This is purely for diagnostic purposes
            android.util.Log.e("ProfileAndSettings", "Failed to show notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
