package com.tehmou.rxbookapp.activities;

import com.tehmou.rxbookapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Pawel Polanski on 5/27/15.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChooseRepositoryActivityTest {
    @Rule
    public ActivityTestRule<ChooseRepositoryActivity> activityRule = new ActivityTestRule<>(ChooseRepositoryActivity.class);

    @Test
    public void testInitialActivityState() {
        onView(withId(R.id.repositories_list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.repositories_status_text)).check(matches(isDisplayed()));
        onView(withId(R.id.repositories_search)).check(matches(isDisplayed()));
        onView(withId(R.id.repositories_status_text)).check(matches(withText((""))));
    }

    @Test
    public void testCanPerformInsertingSearchText() {
        onView(withId(R.id.repositories_search)).perform(typeText("rx-android-architecture"));
    }
}
