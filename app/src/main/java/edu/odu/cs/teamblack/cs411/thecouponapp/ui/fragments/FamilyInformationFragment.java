package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class FamilyInformationFragment extends Fragment {

    private LinearLayout containerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.family_information_activity, container, false);

        containerLayout = view.findViewById(R.id.linearLayoutContainer);
        Button addButton = view.findViewById(R.id.buttonAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyInfoSection(inflater);
            }
        });

        return view;
    }

    private void addFamilyInfoSection(LayoutInflater inflater) {
        View familyInfoSection = inflater.inflate(R.layout.family_info_layout, containerLayout, false);
        containerLayout.addView(familyInfoSection, containerLayout.getChildCount() - 2); // Add before the Save button
    }
}
