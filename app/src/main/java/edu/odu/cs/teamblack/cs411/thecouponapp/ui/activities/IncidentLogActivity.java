package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.utils.SharedPreferences;

public class IncidentLogActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, incident_log, wake_words, communications, local_resources, emergency_contacts, profile_and_settings, safe_exit, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_log);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        incident_log = findViewById(R.id.incident_log);
        wake_words = findViewById(R.id.wake_words);
        communications = findViewById(R.id.communications);
        emergency_contacts = findViewById(R.id.emergency_contacts);
        local_resources = findViewById(R.id.local_resources);
        profile_and_settings = findViewById(R.id.profile_and_settings);
        safe_exit = findViewById(R.id.safe_exit);
        logout = findViewById(R.id.logout);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(IncidentLogActivity.this, HomeActivity.class);
            }
        });

        incident_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        communications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(IncidentLogActivity.this, CommunicationsActivity.class);
            }
        });

        local_resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(IncidentLogActivity.this, LocalResourcesActivity.class);
            }
        });

        profile_and_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(IncidentLogActivity.this, ProfileAndSettingsActivity.class);
            }
        });

        safe_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                safeExit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.safe_exit_btn)
        {
            safeExit();
        }
        return true;
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        if (item.getItemId() == R.id.nav_home)
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//
//        if (item.getItemId() == R.id.nav_incident_log)
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IncidentLogFragment()).commit();
//
//        if (item.getItemId() == R.id.nav_wake_words)
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//
//        if (item.getItemId() == R.id.nav_communications)
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//
//        if (item.getItemId() == R.id.nav_emergency_contacts) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmergencyContactsFragment()).commit();
//        }
//
//        if (item.getItemId() == R.id.nav_local_resources)
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//
//        if (item.getItemId() == R.id.nav_profileSettings)
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileSettingsFragment()).commit();
//
//        if (item.getItemId() == R.id.nav_safe_exit)
//            safeExit();
//
//        if (item.getItemId() == R.id.nav_logout)
//        {
//            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
//            // Perform logout action
//            logout();
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void safeExit() {
        // Implement your logout logic here
        // For example, clearing user session, resetting preferences, etc.
        // Once logged out, you may navigate back to the login screen
        Intent intent = new Intent(IncidentLogActivity.this, FacadeActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }

    private void logout() {
        SharedPreferences.clearAccessToken(this); // Clear the access token
        Intent intent = new Intent(IncidentLogActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }
}