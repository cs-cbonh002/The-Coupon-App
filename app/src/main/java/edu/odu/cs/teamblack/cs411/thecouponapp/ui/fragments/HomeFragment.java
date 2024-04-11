package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;

public class HomeFragment extends Fragment {

    // Declare a reference to MainActivity
    private MainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Check if the attached context is an instance of MainActivity
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        } else {
            // Throw an exception or handle the case where the attached context is not MainActivity
            throw new ClassCastException("HomeFragment's context is not an instance of MainActivity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Find buttons by their IDs
        Button incidentLogButton = view.findViewById(R.id.incidentLogButton);
        Button wakeWordsButton = view.findViewById(R.id.wakeWordsButton);
        Button communicationsButton = view.findViewById(R.id.communicationsButton);
        Button emergencyContactsButton = view.findViewById(R.id.emergencyContactsButton);
        Button localResourcesButton = view.findViewById(R.id.localResourcesButton);
        Button settingsButton = view.findViewById(R.id.settingsButton);

        // Set click listeners for each button
        incidentLogButton.setOnClickListener(v -> navigateToIncidentLogsFragment());
        wakeWordsButton.setOnClickListener(v -> navigateToWakeWordsFragment());
        communicationsButton.setOnClickListener(v -> navigateToCommunicationsFragment());
        emergencyContactsButton.setOnClickListener(v -> navigateToEmergencyContactsFragment());
        localResourcesButton.setOnClickListener(v -> navigateToLocalResourcesFragment());
        settingsButton.setOnClickListener(v -> navigateToSettingsFragment());

        return view;
    }

    // Methods to navigate to respective fragments
    private void navigateToIncidentLogsFragment() {
        if (mainActivity != null) {
            mainActivity.navigateTo(new IncidentLogsFragment(), true);
        }
    }

    private void navigateToWakeWordsFragment() {
        if (mainActivity != null) {
            mainActivity.navigateTo(new WakeWordsFragment(), true);
        }
    }

    private void navigateToCommunicationsFragment() {
        if (mainActivity != null) {
            mainActivity.navigateTo(new CommunicationsFragment(), true);
        }
    }

    private void navigateToEmergencyContactsFragment() {
        if (mainActivity != null) {
            mainActivity.navigateTo(new EmergencyContactsFragment(), true);
        }
    }

    private void navigateToLocalResourcesFragment() {
        if (mainActivity != null) {
            mainActivity.navigateTo(new LocalResourcesFragment(), true);
        }
    }

    private void navigateToSettingsFragment() {
        if (mainActivity != null) {
            mainActivity.navigateTo(new SettingsFragment(), true);
        }
    }
}
