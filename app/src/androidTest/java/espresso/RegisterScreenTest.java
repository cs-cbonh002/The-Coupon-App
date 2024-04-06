package espresso;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterScreenTest {
    @Rule
    public ActivityScenarioRule<RegisterActivity> activityRule =
            new ActivityScenarioRule<RegisterActivity>(RegisterActivity.class);

    @Test
    public void welcomeBannerTest()
    {
        onView(withId(R.id.welcomeBanner)).check(matches(isDisplayed()));
        onView(withId(R.id.welcomeBanner)).check(isCompletelyAbove(withId(R.id.userNameField)));
        onView(withId(R.id.welcomeBanner)).check(matches(isNotClickable()));
    }

    @Test
    public void userNameFieldTest()
    {
        onView(withId(R.id.userNameField)).check(matches(isDisplayed()));
        onView(withId(R.id.userNameField)).check(isCompletelyBelow(withId(R.id.welcomeBanner)));
        onView(withId(R.id.userNameField)).check(isCompletelyAbove(withId(R.id.passwordField)));
        onView(withId(R.id.userNameField)).perform(typeText("typing test"));
    }

    @Test
    public void passwordFieldTest()
    {
        onView(withId(R.id.passwordField)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordField)).check(isCompletelyBelow(withId(R.id.userNameField)));
        onView(withId(R.id.passwordField)).check(isCompletelyAbove(withId(R.id.confirmPasswordField)));
        onView(withId(R.id.passwordField)).perform(typeText("typing test"));
    }

    @Test
    public void confirmPasswordFieldTest()
    {
        onView(withId(R.id.confirmPasswordField)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmPasswordField)).check(isCompletelyBelow(withId(R.id.passwordField)));
        onView(withId(R.id.confirmPasswordField)).check(isCompletelyAbove(withId(R.id.newAccountMessage)));
        onView(withId(R.id.confirmPasswordField)).perform(typeText("typing test"));
    }

    @Test
    public void newAccountMessageTest()
    {
        onView(withId(R.id.newAccountMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.newAccountMessage)).check(isCompletelyBelow(withId(R.id.confirmPasswordField)));
        onView(withId(R.id.passwordField)).check(isCompletelyAbove(withId(R.id.login_button)));
        onView(withId(R.id.newAccountMessage)).check(matches(isNotClickable()));
    }

    @Test
    public void registerButtonTest()
    {
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        onView(withId(R.id.register_button)).check(isCompletelyBelow(withId(R.id.confirmPasswordField)));
        onView(withId(R.id.register_button)).check(isCompletelyAbove(withId(R.id.newAccountMessage)));
        onView(withId(R.id.register_button)).check(matches(isClickable()));
    }
}
