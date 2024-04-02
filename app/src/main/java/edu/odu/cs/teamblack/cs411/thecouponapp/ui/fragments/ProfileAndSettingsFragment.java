package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.FacadeActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.LoginActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.utils.SharedPreferences;

public class ProfileAndSettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Button Family_Info_Button, Emergency_Contact_Button, Danger_Assessment_Button, Wake_Word_Setting_Button, Theme_Setting_Button, Feature_Setting_Button, Save_Button;
        EditText First_Name, Last_Name, Email, Date_Of_Birth, Gender;


        return inflater.inflate(R.layout.fragment_profile_and_settings, container, false);
    }
}