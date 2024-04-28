package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters.EmergencyContactsAdapter;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels.EmergencyContactsViewModel;

public class EmergencyContactsFragment extends Fragment {
    private EmergencyContactsViewModel viewModel;
    private EmergencyContactsAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton add_contact_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emergency_contacts_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_emergency_contacts);
        add_contact_button = view.findViewById(R.id.add_contact_button);

        viewModel = new ViewModelProvider(this).get(EmergencyContactsViewModel.class);
        adapter = new EmergencyContactsAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllEmergencyContacts().observe(getViewLifecycleOwner(), contacts -> {
            Log.d("LiveData", "Contacts updated, size: " + contacts.size());
            adapter.setEmergencyContacts(contacts);
        });


        add_contact_button.setOnClickListener(v -> { // Now fab is initialized, so this should work
            EmergencyContactsDetailsFragment addFragment = EmergencyContactsDetailsFragment.newInstance(null);
            addFragment.show(getParentFragmentManager(), "EmergencyContactsDetailsFragment");
        });

        adapter.setOnItemClickListener(emergencyContact -> {
            EmergencyContactsDetailsFragment editFragment = EmergencyContactsDetailsFragment.newInstance(emergencyContact);
            editFragment.show(getParentFragmentManager(), "EmergencyContactsDetailsFragment");
        });
    }
}