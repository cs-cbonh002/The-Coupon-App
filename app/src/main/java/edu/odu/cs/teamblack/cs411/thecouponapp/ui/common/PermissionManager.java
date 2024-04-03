package edu.odu.cs.teamblack.cs411.thecouponapp.ui.common;

import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {

    public interface PermissionResultListener {
        void onPermissionGranted(String permission);
        void onPermissionDenied(String permission);
        void onPermissionDeniedForever(String permission);
    }

    private final Fragment fragment;
    private final Map<String, PermissionResultListener> listeners = new HashMap<>();
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public PermissionManager(Fragment fragment) {
        this.fragment = fragment;
        initLauncher();
    }

    private void initLauncher() {
        requestPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    // The permission being requested is deduced by checking against stored listeners
                    for (Map.Entry<String, PermissionResultListener> entry : listeners.entrySet()) {
                        if (ContextCompat.checkSelfPermission(fragment.requireContext(), entry.getKey()) ==
                                PackageManager.PERMISSION_DENIED) {
                            // Found the permission that was requested and denied
                            handlePermissionResult(entry.getKey(), isGranted);
                            break; // Stop after handling the first denied permission
                        }
                    }
                });
    }

    public void requestPermission(String permission, PermissionResultListener listener) {
        if (listeners.containsKey(permission)) {
            // Already requesting this permission
            return;
        }
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionGranted(permission);
            return;
        }

        listeners.put(permission, listener);

        if (fragment.shouldShowRequestPermissionRationale(permission)) {
            // Show UI rationale here if needed and afterwards call:
            // requestPermissionLauncher.launch(permission);
            listener.onPermissionDenied(permission);
        } else {
            // Directly ask for the permission without rationale
            requestPermissionLauncher.launch(permission);
        }
    }

    private void handlePermissionResult(String permission, boolean isGranted) {
        PermissionResultListener listener = listeners.remove(permission);
        if (listener == null) {
            return; // Listener was not registered, this should not happen
        }

        if (isGranted) {
            listener.onPermissionGranted(permission);
        } else {
            if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                listener.onPermissionDeniedForever(permission);
            } else {
                listener.onPermissionDenied(permission);
            }
        }
    }
    public void checkPermission(String permission, PermissionResultListener listener) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionGranted(permission);
        } else {
            if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                listener.onPermissionDeniedForever(permission);
            } else {
                listener.onPermissionDenied(permission);
            }
        }
    }
}
