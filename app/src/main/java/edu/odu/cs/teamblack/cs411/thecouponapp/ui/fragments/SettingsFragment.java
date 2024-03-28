package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationCompat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;

public class SettingsFragment extends Fragment {

    private static final String CHANNEL_ID = "edu.odu.cs.teamblack.cs411.thecouponapp.settings";
    private ToggleButton locationButton;
    private TextView locationCoordinateTextView;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize permission launcher for location permissions
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required for this feature", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize permission launcher for notification permissions
        requestNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                showNotification();
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        createNotificationChannel();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        locationCoordinateTextView = view.findViewById(R.id.LocationCoord);
        locationButton = view.findViewById(R.id.locationButton);
        locationButton.setChecked(checkPermissions());
        locationButton.setOnClickListener(v -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION));

        Button wakeWordButton = view.findViewById(R.id.wakeWordButton);
        wakeWordButton.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.navigateToWakeWordsFragment();
            }
        });

        Button notificationsButton = view.findViewById(R.id.notificationsButton);
        notificationsButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                showNotification();
            }
        });

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with showing notification
                buildAndNotify();
            } else {
                // Permission not granted, request it
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Below Android 13 (API level 33), continue without the permission check
            buildAndNotify();
        }
    }

    @SuppressLint("MissingPermission")
    private void buildAndNotify() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.lock_icon)
                .setContentTitle("The Coupon App")
                .setContentText("Hello from The Coupon App, This is a simple Notification")
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            // Getting the last known location
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    locationCoordinateTextView.setText(String.format("Latitude:%s\nLongitude:%s", location.getLatitude(), location.getLongitude()));
                }
            });
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationButton.setChecked(checkPermissions());
    }
}
