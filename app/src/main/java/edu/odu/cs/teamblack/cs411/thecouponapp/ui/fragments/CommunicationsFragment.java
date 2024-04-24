package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class CommunicationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.communications_activity, container, false);
        Button Save = view.findViewById(R.id.save_button);
        Switch Email = view.findViewById(R.id.Email_Switch);
        Switch GPS_Email = view.findViewById(R.id.GPS_Email_Switch);
        Switch Message_Switch = view.findViewById(R.id.Message_Switch);
        Switch Emergency = view.findViewById(R.id.Emergency_Contact_Phone);
        Switch Phone = view.findViewById(R.id.GPS_SMS_Switch);
        Switch Authorities = view.findViewById(R.id.Authority_Phone_Call);

        return view;
    }
}