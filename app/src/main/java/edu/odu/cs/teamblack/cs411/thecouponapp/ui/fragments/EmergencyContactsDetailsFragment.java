package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.EmergencyContactsViewModel;

public class EmergencyContactsDetailsFragment extends BottomSheetDialogFragment {
    // UI element variables
    private static final String ARG_EMERGENCY_CONTACT = "emergencyContact";
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText relationshipInput;
    private TextInputEditText phoneInput;
    private TextInputEditText emailInput;
    private SwitchMaterial primarySwitch;
    private CheckBox callCheckbox;
    private CheckBox textCheckbox;
    private CheckBox emailCheckbox;
    private MaterialButton saveButton;
    private ImageButton closeButton;
    public static final int COMMUNICATION_CALL = 1;   // 001
    public static final int COMMUNICATION_TEXT = 2;   // 010
    public static final int COMMUNICATION_EMAIL = 4;  // 100

    private EmergencyContactsViewModel viewModel;
    private EmergencyContact emergencyContact; // For editing existing contacts

    public static EmergencyContactsDetailsFragment newInstance(EmergencyContact emergencyContact) {
        EmergencyContactsDetailsFragment fragment = new EmergencyContactsDetailsFragment();
        Bundle args = new Bundle();
        if (emergencyContact != null) {
            args.putParcelable(ARG_EMERGENCY_CONTACT, emergencyContact);
        } else {
            args.putBoolean("isNewContact", true);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergency_contacts_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EmergencyContactsViewModel.class);
        initializeViews(view);
        setupListeners();

        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_EMERGENCY_CONTACT)) {
                emergencyContact = getArguments().getParcelable(ARG_EMERGENCY_CONTACT);
                if (emergencyContact != null) {
                    populateViews(emergencyContact);
                }
            } else {
                // New emergency contact, initialize fields as needed
                firstNameInput.setText("");
                lastNameInput.setText("");
                relationshipInput.setText("");
                phoneInput.setText("");
                emailInput.setText("");
                primarySwitch.setChecked(false);
                callCheckbox.setChecked(false);
                textCheckbox.setChecked(false);
                emailCheckbox.setChecked(false);
            }
        }

    }

    private void initializeViews(View view) {
        firstNameInput = view.findViewById(R.id.firstname_input);
        lastNameInput = view.findViewById(R.id.lastname_input);
        relationshipInput = view.findViewById(R.id.relationship_input);
        phoneInput = view.findViewById(R.id.phone_input);
        emailInput = view.findViewById(R.id.email_input);
        primarySwitch = view.findViewById(R.id.primary_switch);
        callCheckbox = view.findViewById(R.id.call_checkbox);
        textCheckbox = view.findViewById(R.id.text_checkbox);
        emailCheckbox = view.findViewById(R.id.email_checkbox);
        saveButton = view.findViewById(R.id.save_button);
        closeButton = view.findViewById(R.id.close_button);
    }

    private void setupListeners() {
        closeButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> saveEmergencyContact());
        primarySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle primary contact toggle
            if (isChecked) {
                // Update existing primary contact (if any)
                EmergencyContact currentPrimary = viewModel.getPrimaryEmergencyContact();
                if (currentPrimary!= null && currentPrimary.id!= emergencyContact.id) {
                    currentPrimary.isPrimary = false;
                    viewModel.update(currentPrimary);
                }
            }
        });

        // Add text watchers to input fields
        firstNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        relationshipInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateFields() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String relationship = relationshipInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        boolean isValid =!firstName.isEmpty() &&!lastName.isEmpty() &&!relationship.isEmpty() &&!(phone.isEmpty() && email.isEmpty());
        saveButton.setEnabled(isValid);
    }

    private void populateViews(EmergencyContact emergencyContact) {
        Log.d("EmergencyContactsDetailsFragment", "Populating views with: " + emergencyContact.toString());
        firstNameInput.setText(emergencyContact.firstName);
        lastNameInput.setText(emergencyContact.lastName);
        relationshipInput.setText(emergencyContact.relationship);
        phoneInput.setText(emergencyContact.phoneNumber);
        emailInput.setText(emergencyContact.email);
        primarySwitch.setChecked(emergencyContact.isPrimary);

        // Set communication preferences checkboxes
        callCheckbox.setChecked((emergencyContact.communicationPreferences & COMMUNICATION_CALL)!= 0);
        textCheckbox.setChecked((emergencyContact.communicationPreferences & COMMUNICATION_TEXT)!= 0);
        emailCheckbox.setChecked((emergencyContact.communicationPreferences & COMMUNICATION_EMAIL)!= 0);

        if (emergencyContact.isPrimary) {
            primarySwitch.setEnabled(false);
        }
    }

    private void ensureOnlyOnePrimaryContact(EmergencyContact emergencyContact) {
        EmergencyContact currentPrimaryContact = viewModel.getPrimaryEmergencyContact();

        if (currentPrimaryContact!= null && currentPrimaryContact!= emergencyContact) {
            if (currentPrimaryContact.isPrimary) {
                currentPrimaryContact.setPrimary(false);
                viewModel.update(currentPrimaryContact);
            }
        }

        if (emergencyContact!= null && emergencyContact.isPrimary) {
            emergencyContact.setPrimary(true);
        }
    }

    private void saveEmergencyContact() {
        if (emergencyContact == null) {
            emergencyContact = new EmergencyContact();
        }

        ensureOnlyOnePrimaryContact(emergencyContact);

        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String relationship = relationshipInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Input validation
        if (!saveButton.isEnabled()) {
            Toast.makeText(getContext(), "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update fields of emergencyContact
        emergencyContact.firstName = firstName;
        emergencyContact.lastName = lastName;
        emergencyContact.relationship = relationship;
        emergencyContact.phoneNumber = phone;
        emergencyContact.email = email;
        emergencyContact.isPrimary = primarySwitch.isChecked();

        // Communication preferences (using flags)
        int communicationFlags = 0;
        if (callCheckbox.isChecked()) communicationFlags |= 1; // 001
        if (textCheckbox.isChecked()) communicationFlags |= 2; // 010
        if (emailCheckbox.isChecked()) communicationFlags |= 4; // 100
        emergencyContact.communicationPreferences = communicationFlags;

        // Save using ViewModel
        viewModel.update(emergencyContact);
        Log.d("EmergencyContactsDetailsFragment", "Saved emergency contact: " + emergencyContact.toString());
        dismiss();
    }
}