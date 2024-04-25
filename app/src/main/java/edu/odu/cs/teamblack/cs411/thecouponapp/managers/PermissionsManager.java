package edu.odu.cs.teamblack.cs411.thecouponapp.managers;

import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PermissionsManager {
    public interface MultiplePermissionsListener {
        void onAllPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
        void onPermissionsDeniedForever(List<String> deniedForeverPermissions);
    }

    private final Fragment fragment;
    private MultiplePermissionsListener multiplePermissionsListener;
    private ActivityResultLauncher<String[]> permissionsLauncher;

    public PermissionsManager(Fragment fragment) {
        this.fragment = fragment;
        initLauncher();
    }

    private void initLauncher() {
        permissionsLauncher = fragment.registerForActivityResult(
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

    public void requestPermissions(String[] permissions, MultiplePermissionsListener listener) {
        List<String> permissionsNotGranted = Arrays.stream(permissions)
                .filter(permission -> ContextCompat.checkSelfPermission(fragment.requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
                .collect(Collectors.toList());

        if (permissionsNotGranted.isEmpty()) {
            listener.onAllPermissionsGranted();
        } else {
            multiplePermissionsListener = listener;
            permissionsLauncher.launch(permissionsNotGranted.toArray(new String[0]));
        }
    }
}
