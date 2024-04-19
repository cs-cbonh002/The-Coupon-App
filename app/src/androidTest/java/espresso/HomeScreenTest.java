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
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.LoginActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.MainActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.RegisterActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.HomeFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeScreenTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init()
    {
        activityTestRule.getActivity().getSupportFragmentManager()
                .beginTransaction();
    }

    @Test
    public void incidentLogButtonTest()
    {
        onView(withId(R.id.incidentLogButton)).check(matches(isDisplayed()));
        onView(withId(R.id.incidentLogButton)).check(isCompletelyAbove(withId(R.id.wakeWordsButton)));
        onView(withId(R.id.incidentLogButton)).check(matches(isClickable()));

        onView(withId(R.id.incidentLogButton)).perform(click());
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    @Test
    public void wakeWordsButtonTest()
    {
        onView(withId(R.id.wakeWordsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.wakeWordsButton)).check(isCompletelyBelow(withId(R.id.incidentLogButton)));
        onView(withId(R.id.wakeWordsButton)).check(isCompletelyAbove(withId(R.id.communicationsButton)));
        onView(withId(R.id.wakeWordsButton)).check(matches(isClickable()));

        onView(withId(R.id.wakeWordsButton)).perform(click());
        onView(withId(R.id.enable_wake_word_control_switch)).check(matches(isDisplayed()));
    }

    @Test
    public void communicationsButtonTest()
    {
        onView(withId(R.id.communicationsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.communicationsButton)).check(isCompletelyBelow(withId(R.id.wakeWordsButton)));
        onView(withId(R.id.communicationsButton)).check(isCompletelyAbove(withId(R.id.emergencyContactsButton)));
        onView(withId(R.id.communicationsButton)).check(matches(isClickable()));

        onView(withId(R.id.communicationsButton)).perform(click());
        onView(withId(R.id.Email_Switch)).check(matches(isDisplayed()));
    }

    @Test
    public void emergencyContactsButtonTest()
    {
        onView(withId(R.id.emergencyContactsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.emergencyContactsButton)).check(isCompletelyBelow(withId(R.id.communicationsButton)));
        onView(withId(R.id.emergencyContactsButton)).check(isCompletelyAbove(withId(R.id.localResourcesButton)));
        onView(withId(R.id.emergencyContactsButton)).check(matches(isClickable()));

        onView(withId(R.id.emergencyContactsButton)).perform(click());
        onView(withId(R.id.selectFamilyMemberButton)).check(matches(isDisplayed()));
    }

    @Test
    public void localResourcesButtonTest()
    {
        onView(withId(R.id.localResourcesButton)).check(matches(isDisplayed()));
        onView(withId(R.id.localResourcesButton)).check(isCompletelyBelow(withId(R.id.emergencyContactsButton)));
        onView(withId(R.id.localResourcesButton)).check(isCompletelyAbove(withId(R.id.settingsButton)));
        onView(withId(R.id.localResourcesButton)).check(matches(isClickable()));

        onView(withId(R.id.localResourcesButton)).perform(click());
        onView(withId(R.id.editTextZipcode)).check(matches(isDisplayed()));
    }

    @Test
    public void settingsButtonTest()
    {
        onView(withId(R.id.settingsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.settingsButton)).check(isCompletelyBelow(withId(R.id.localResourcesButton)));
        onView(withId(R.id.settingsButton)).check(matches(isClickable()));

        onView(withId(R.id.settingsButton)).perform(click());
        onView(withId(R.id.accountGroup)).check(matches(isDisplayed()));
    }

}
