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

public class LocalResourcesActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, incident_log, wake_words, communications, local_resources, emergency_contacts, profile_and_settings, safe_exit, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_resources);

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
                redirectActivity(LocalResourcesActivity.this, HomeActivity.class);
            }
        });

        incident_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LocalResourcesActivity.this, IncidentLogActivity.class);
            }
        });
        communications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(LocalResourcesActivity.this, CommunicationsActivity.class);
            }
        });

        emergency_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LocalResourcesActivity.this, EmergencyContactsActivity.class);
            }
        });

        local_resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        profile_and_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LocalResourcesActivity.this, EmergencyContactsActivity.class);
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
        Intent intent = new Intent(LocalResourcesActivity.this, FacadeActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }

    private void logout() {
        SharedPreferences.clearAccessToken(this); // Clear the access token
        Intent intent = new Intent(LocalResourcesActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }
}