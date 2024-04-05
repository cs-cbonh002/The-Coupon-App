package edu.odu.cs.teamblack.cs411.thecouponapp.ui.common;

import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PermissionManager {
    public interface PermissionResultListener {
        void onPermissionGranted(String permission);
        void onPermissionDenied(String permission);
        void onPermissionDeniedForever(String permission);
    }

    public interface MultiplePermissionsListener {
        void onAllPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
        void onPermissionsDeniedForever(List<String> deniedForeverPermissions);
    }

    private final Fragment fragment;
    private final Map<String, PermissionResultListener> singlePermissionListeners = new HashMap<>();
    private MultiplePermissionsListener multiplePermissionsListener;
    private ActivityResultLauncher<String[]> multiplePermissionsRequestLauncher;

    public PermissionManager(Fragment fragment) {
        this.fragment = fragment;
        initLaunchers();
    }

    private void initLaunchers() {
        multiplePermissionsRequestLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                    List<String> grantedPermissions = permissions.entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    List<String> deniedPermissions = permissions.keySet().stream()
                            .filter(key -> !grantedPermissions.contains(key))
                            .collect(Collectors.toList());

                    List<String> deniedForeverPermissions = deniedPermissions.stream()
                            .filter(permission -> !fragment.shouldShowRequestPermissionRationale(permission))
                            .collect(Collectors.toList());

                    if (grantedPermissions.size() == permissions.size()) {
                        multiplePermissionsListener.onAllPermissionsGranted();
                    } else {
                        if (!deniedForeverPermissions.isEmpty()) {
                            multiplePermissionsListener.onPermissionsDeniedForever(deniedForeverPermissions);
                        } else {
                            multiplePermissionsListener.onPermissionsDenied(deniedPermissions);
                        }
                    }
                });
    }

    public void requestPermission(String permission, PermissionResultListener listener) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionGranted(permission);
        } else {
            singlePermissionListeners.put(permission, listener);
            multiplePermissionsRequestLauncher.launch(new String[] { permission });
        }
    }

    public void requestPermissions(String[] permissions, MultiplePermissionsListener listener) {
        List<String> permissionsNotGranted = Arrays.stream(permissions)
                .filter(permission -> ContextCompat.checkSelfPermission(fragment.requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
                .collect(Collectors.toList());

        if (permissionsNotGranted.isEmpty()) {
            listener.onAllPermissionsGranted();
        } else {
            multiplePermissionsListener = listener;
            multiplePermissionsRequestLauncher.launch(permissionsNotGranted.toArray(new String[0]));
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
