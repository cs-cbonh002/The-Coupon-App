package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.app.Service;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;


//package edu.odu.cs.teamblack.cs411.thecouponapp.services;
//import javax.swing.DefaultBoundedRangeModel;
//import android.location.location;
import android.os.Build;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.gpsFragment;

public class gpsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gps_spoofing_fragment, container, false);
    }
}

