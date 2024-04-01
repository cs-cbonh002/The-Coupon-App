package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.CommunicationsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.EmergencyContactsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.HomeFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.IncidentLogsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.LocalResourcesFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.ProfileAndSettingsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.SettingsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.WakeWordsFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.utils.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // If savedInstanceState is null, then this is a fresh launch
        if (savedInstanceState == null) {
            navigateToFragment(new HomeFragment());
            // Assuming nav_home is the ID of the navigation menu item for the Home fragment
            MenuItem homeItem = navigationView.getMenu().findItem(R.id.nav_home);
            if (homeItem != null) {
                homeItem.setChecked(true);
            }
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If you want to call super.onBackPressed() only when the callback is disabled
                    if (isEnabled()) {
                        setEnabled(false);
                    } else {
                        MainActivity.super.onBackPressed();
                    }
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_safe_exit) { // Make sure this ID is unique to the toolbar menu
            safeExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        String title = "Home"; // Set default title to "Home"

        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            toolbar.setTitle("Home");
        } else if (id == R.id.nav_incident_log) {
            fragment = new IncidentLogsFragment();
            toolbar.setTitle("Incident Logs");
        } else if (id == R.id.nav_wake_words) {
            fragment = new WakeWordsFragment();
            toolbar.setTitle("Wake Words");
        } else if (id == R.id.nav_communications) {
            fragment = new CommunicationsFragment();
            toolbar.setTitle("Communications");
        } else if (id == R.id.nav_emergency_contacts) {
            fragment = new EmergencyContactsFragment();
            toolbar.setTitle("Emergency Contacts");
        } else if (id == R.id.nav_local_resources) {
            fragment = new LocalResourcesFragment();
            toolbar.setTitle("Local Resources");
        } else if (id == R.id.nav_profile_and_settings) {
            fragment = new ProfileAndSettingsFragment();
            toolbar.setTitle("Profile and Settings");
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
            logout();
            return true;
        } else if (id == R.id.nav_safe_exit) {
            safeExit();
            return true;
        } else {
            return false;
        }

        navigateToFragment(fragment);

        // Set the title for the toolbar
        if (toolbar != null) {
            toolbar.setTitle(menuItem.getTitle());
        }
        // Highlight the selected item and close the drawer
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void navigateToWakeWordsFragment() {
        navigateToFragment(new WakeWordsFragment());
    }

    private void safeExit() {
        Intent intent = new Intent(MainActivity.this, FacadeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void logout() {
        SharedPreferences.clearAccessToken(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
