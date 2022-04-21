package com.group24.interactivediary.activities;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.group24.interactivediary.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateEntryAndEntryDetailsTest {

    @Rule
    public ActivityTestRule<LoginSignupActivity> mActivityTestRule = new ActivityTestRule<>(LoginSignupActivity.class);

    @Test
    public void createEntryAndEntryDetailsTest2() {
        String testUsername = Helpers.genTestCredential(7);
        String testPassword = Helpers.genTestCredential(7);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.signupButton), withText("Sign Up"),
                        childAtPosition(
                                withId(R.id.signupButtonExpandableLayout),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.usernameEditTextSignup),
                        childAtPosition(
                                withId(R.id.signupExpandableLayout),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(testUsername), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.passwordEditTextSignup),
                        childAtPosition(
                                withId(R.id.signupExpandableLayout),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(testPassword), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.signupGoButton), withText("Sign Up"),
                        childAtPosition(
                                withId(R.id.signupExpandableLayout),
                                3),
                        isDisplayed()));
        materialButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.homeCreateEntryButton), withContentDescription("Create Entry"),
                        childAtPosition(
                                withId(R.id.homeRelativeLayout),
                                3),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.entryCreateTitleEditText),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("private entry"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.entryCreateTextEditText),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("contents"), closeSoftKeyboard());

        ViewInteraction materialRadioButton = onView(
                allOf(withId(R.id.createEntryPrivateRadioButton), withText("Private (only you can see and edit this entry)"),
                        childAtPosition(
                                withId(R.id.createEntryRadioGroup),
                                0),
                        isDisplayed()));
        materialRadioButton.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.createEntryPostButton), withText("Post"),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                6),
                        isDisplayed()));
        materialButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(
                withId(R.id.listviewRecyclerView));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.entryDetailsTitleTextView), withText("private entry"), isDisplayed()));
        textView.check(matches(withText("private entry")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.entryDetailsAuthorTextView), withText(testUsername), isDisplayed()));
        textView2.check(matches(withText(testUsername)));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.entryDetailsTimestampTextView), withText("just now"), isDisplayed()));
        textView3.check(matches(withText("just now")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.entryDetailsTextTextView), withText("contents"), isDisplayed()));
        textView4.check(matches(withText("contents")));

        ViewInteraction textView5 = onView(
                allOf(withText("Entry Details"), isDisplayed()));
        textView5.check(matches(withText("Entry Details")));

        ViewInteraction button = onView(
                allOf(withId(R.id.entryDetailsSettingsButton), withText("Edit entry"), isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
