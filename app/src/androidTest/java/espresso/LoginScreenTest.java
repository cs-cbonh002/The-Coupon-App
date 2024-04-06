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
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.LoginActivity;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginScreenTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<LoginActivity>(LoginActivity.class);


    @Test
    public void welcomeBannerTest()
    {
        onView(withId(R.id.welcomeBanner)).check(matches(isDisplayed()));
        onView(withId(R.id.welcomeBanner)).check(isCompletelyAbove(withId(R.id.userNameField)));
        onView(withId(R.id.welcomeBanner)).check(matches(isNotClickable()));
    }

    @Test
    public void loginButtonTest()
    {
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(isCompletelyBelow(withId(R.id.passwordField)));
        onView(withId(R.id.loginButton)).check(isCompletelyAbove(withId(R.id.newAccountMessage)));
        onView(withId(R.id.loginButton)).check(matches(isClickable()));
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
        onView(withId(R.id.passwordField)).check(isCompletelyAbove(withId(R.id.loginButton)));
        onView(withId(R.id.passwordField)).perform(typeText("typing test"));
    }

    @Test
    public void newAccountMessageTest()
    {
        onView(withId(R.id.newAccountMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.newAccountMessage)).check(isCompletelyBelow(withId(R.id.loginButton)));
        onView(withId(R.id.newAccountMessage)).check(isCompletelyAbove(withId(R.id.registerButton)));
        onView(withId(R.id.newAccountMessage)).check(matches(isNotClickable()));
    }

    @Test
    public void registerButtonTest()
    {
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.registerButton)).check(isCompletelyBelow(withId(R.id.newAccountMessage)));
        onView(withId(R.id.registerButton)).check(matches(isClickable()));
    }
}
