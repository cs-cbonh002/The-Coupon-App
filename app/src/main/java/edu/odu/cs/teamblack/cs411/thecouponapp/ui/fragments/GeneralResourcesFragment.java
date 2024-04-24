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

public class GeneralResourcesFragment extends Fragment {

    private Button ndvhButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_general_resources, container, false);

        ndvhButton = rootView.findViewById(id.ndvhButton);

        ndvhButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalQuery = "https://www.thehotline.org";

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
        });

        return rootView;
    }






}