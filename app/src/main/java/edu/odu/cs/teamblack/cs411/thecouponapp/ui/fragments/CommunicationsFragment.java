package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class CommunicationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Switch Email, GPS_Email,Message, Emergency, Phone, Authorities;
        return inflater.inflate(R.layout.activity_communications, container, false);
    }
}