package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;



public class ProfileSettingsFragment extends Fragment {

    View view;
    Button Familybutton;
    Button Emergencybutton;
    Button DangerAssebutton;
    Button WakeWordbutton;
    Button Themebutton;
    Button Featurebutton;
    Button Savebutton;
    EditText FirstNamefield;
    EditText LastNamefield;
    EditText Emailfield;
    EditText DOBfield;
    EditText Genderfield;

    public ProfileSettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_profilesettings, container, false);
        // Reference Edittext fields
        FirstNamefield =  view.findViewById(R.id.editTextFirstName);
        LastNamefield = view.findViewById(R.id.editTextLastName);
        Emailfield = view.findViewById(R.id.editTextEmail);
        DOBfield = view.findViewById(R.id.editTextDOB);
        Genderfield = view.findViewById(R.id.editTextGender);
        // Reference Buttons
        Familybutton = view.findViewById(R.id.familyInfoButton);
        Emergencybutton = view.findViewById(R.id.emergencyContactsButton);
        DangerAssebutton = view.findViewById(R.id.dangerAssessmentButton);
        WakeWordbutton = view.findViewById(R.id.wakeWordSettingsButton);
        Themebutton = view.findViewById(R.id.themeSettingsButton);
        Featurebutton = view.findViewById(R.id.featureSettingsButton);
        Savebutton = view.findViewById(R.id.Savebutton);


        return view;
    }

}