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
public class WakeWordsScreenTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void openFragmentFromNavigationDrawer()
    {
        // Open the navigation drawer
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on the menu option that corresponds to the fragment you want to test
        onView(ViewMatchers.withId(R.id.nav_wake_words)).perform(click());

    }

    @Test
    public void enableWakeWordsControlSwitchTest()
    {
        onView(ViewMatchers.withId(R.id.enable_wake_word_control_switch))
                .check(matches(isDisplayed()));
        onView(withId(R.id.enable_wake_word_control_switch))
                .check(isCompletelyAbove(withId(R.id.keyword_title_1)));
        onView(withId(R.id.enable_wake_word_control_switch)).check(matches(isClickable()));
    }

    @Test
    public void keyword1Test()
    {
        onView(withId(R.id.keyword_title_1)).check(matches(isDisplayed()));
        onView(withId(R.id.keyword_title_1))
                .check(isCompletelyBelow(withId(R.id.enable_wake_word_control_switch)));
        onView(withId(R.id.keyword_title_1))
                .check(isCompletelyAbove(withId(R.id.keyword_subtitle_1)));
        onView(withId(R.id.keyword_title_1)).check(matches(isNotClickable()));

        onView(ViewMatchers.withId(R.id.keyword_subtitle_1)).check(matches(isDisplayed()));
        onView(withId(R.id.keyword_subtitle_1))
                .check(isCompletelyBelow(withId(R.id.keyword_title_1)));
        onView(withId(R.id.keyword_subtitle_1))
                .check(isCompletelyAbove(withId(R.id.keyword_menu_1)));
        onView(withId(R.id.keyword_subtitle_1)).check(matches(isNotClickable()));

        onView(ViewMatchers.withId(R.id.keyword_menu_1)).check(matches(isDisplayed()));
        onView(withId(R.id.keyword_menu_1))
                .check(isCompletelyBelow(withId(R.id.keyword_subtitle_1)));
        onView(withId(R.id.keyword_menu_1))
                .check(isCompletelyAbove(withId(R.id.keyword_title_2)));
        onView(withId(R.id.keyword_menu_1)).check(matches(isNotClickable()));

        onView(ViewMatchers.withId(R.id.keyword_dropdown_1)).check(matches(isDisplayed()));
        onView(withId(R.id.keyword_dropdown_1))
                .check(isCompletelyBelow(withId(R.id.keyword_subtitle_1)));
        onView(withId(R.id.keyword_dropdown_1))
                .check(isCompletelyAbove(withId(R.id.keyword_title_2)));
        onView(withId(R.id.keyword_dropdown_1)).check(matches(isClickable()));
    }



}