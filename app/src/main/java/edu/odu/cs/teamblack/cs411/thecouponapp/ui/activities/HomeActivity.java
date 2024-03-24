package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.EmergencyContactsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.HomeFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.IncidentLogFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.ProfileSettingsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.utils.SharedPreferences;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_home)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        if (item.getItemId() == R.id.nav_incident_log)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IncidentLogFragment()).commit();

        if (item.getItemId() == R.id.nav_wake_words)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        if (item.getItemId() == R.id.nav_communications)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        if (item.getItemId() == R.id.nav_emergency_contacts) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmergencyContactsFragment()).commit();
        }

        if (item.getItemId() == R.id.nav_local_resources)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        if (item.getItemId() == R.id.nav_profileSettings)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileSettingsFragment()).commit();

        if (item.getItemId() == R.id.nav_safe_exit)
            safeExit();

        if (item.getItemId() == R.id.nav_logout)
        {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
            // Perform logout action
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
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
        Intent intent = new Intent(HomeActivity.this, FacadeActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }

    private void logout() {
        SharedPreferences.clearAccessToken(this); // Clear the access token
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }



}