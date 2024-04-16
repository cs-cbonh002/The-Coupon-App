package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.R.id;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.Local;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;

import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class LocalResourcesFragment extends Fragment {

    private EditText zipCode;
    private Button searchButton, savedResourcesButton;
    private WebView webView;
    private CheckBox housingCheckBox, shelterCheckBox, legalCheckBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.local_resources_activity, container, false);

        zipCode = rootView.findViewById(R.id.editTextZipcode);
        searchButton = rootView.findViewById(id.searchButton);
        savedResourcesButton = rootView.findViewById(R.id.savedResourcesButton);

        housingCheckBox = rootView.findViewById(R.id.housingCheckBox);
        shelterCheckBox = rootView.findViewById(R.id.shelterCheckBox);
        legalCheckBox = rootView.findViewById(R.id.legalCheckBox);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        savedResourcesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSavedResourcesFragment();
            }
        });

        return rootView;
    }


    private void performSearch() {
        // Get the search query from the EditText field
        String query = zipCode.getText().toString().trim();

        // Append the text of selected CheckBox(es) to the search query
        StringBuilder searchQueryBuilder = new StringBuilder(query);

        if (housingCheckBox.isChecked()) {
            searchQueryBuilder.append(" Housing");
        }

        if (shelterCheckBox.isChecked()) {
            searchQueryBuilder.append(" Shelters");
        }

        if (legalCheckBox.isChecked()) {
            searchQueryBuilder.append(" Legal");
        }

        // Get the final search query
        String finalQuery = searchQueryBuilder.toString();

        if (!finalQuery.isEmpty()) {
            // Create a new instance of the WebViewFragment
            WebViewFragment webViewFragment = new WebViewFragment();

            // Pass the search query as an argument to the WebViewFragment
            Bundle args = new Bundle();
            args.putString("searchQuery", finalQuery);
            webViewFragment.setArguments(args);

            // Replace the current fragment with the WebViewFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, webViewFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToSavedResourcesFragment() {
        // Create a new instance of SavedResourcesFragment
        SavedResourcesFragment savedResourcesFragment = new SavedResourcesFragment();

        // Replace the current fragment with the SavedResourcesFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, savedResourcesFragment)
                .addToBackStack(null)  // Add to back stack for navigation back
                .commit();
    }

}
