package espresso;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.HomeFragment;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.WakeWordsFragment;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavMenuTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void openNavigationDrawer()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    }

    @Test
    public void navigateToHomeScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_home)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.incidentLogButton)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToIncidentLogScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_incident_log)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToWakeWordsScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_wake_words)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.enable_wake_word_control_switch)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToCommunicationsScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_communications)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.Email_Switch)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToEmergencyContactsScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_emergency_contacts)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.selectFamilyMemberButton)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToLocalResourcesScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_local_resources)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.editTextZipcode)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToSettingsScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_settings)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.accountGroup)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToLogoutScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_logout)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.welcomeBanner)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToMultipleScreenTest()
    {
        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_settings)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.accountGroup)).check(matches(isDisplayed()));

        // Open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_home)).perform(click());

        // Check if an element on the selected screen is displayed
        // If true, navigation successful
        onView(withId(R.id.wakeWordsButton)).check(matches(isDisplayed()));
    }






}
