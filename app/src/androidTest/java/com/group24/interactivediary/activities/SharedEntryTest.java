package com.group24.interactivediary.activities;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

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
public class SharedEntryTest {

    @Rule
    public ActivityTestRule<LoginSignupActivity> mActivityTestRule = new ActivityTestRule<>(LoginSignupActivity.class);

    @Test
    public void sharedEntryTest() {
        String testUsername = Helpers.genTestCredential(7);
        String testPassword = Helpers.genTestCredential(7);
        String testUsername2 = Helpers.genTestCredential(7);
        String testPassword2 = Helpers.genTestCredential(7);

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

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                withClassName(is("androidx.appcompat.widget.ActionMenuView")),
                                1),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialTextView = onView(
                allOf(withId(androidx.appcompat.R.id.title), withText("Logout"),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.signupButton), withText("Sign Up"),
                        childAtPosition(
                                withId(R.id.signupButtonExpandableLayout),
                                0),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.usernameEditTextSignup),
                        childAtPosition(
                                withId(R.id.signupExpandableLayout),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText(testUsername2), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.passwordEditTextSignup),
                        childAtPosition(
                                withId(R.id.signupExpandableLayout),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(testPassword2), closeSoftKeyboard());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.signupGoButton), withText("Sign Up"),
                        childAtPosition(
                                withId(R.id.signupExpandableLayout),
                                3),
                        isDisplayed()));
        materialButton4.perform(click());

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

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.entryCreateTitleEditText),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.entryCreateTitleEditText),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("shared entry"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.entryCreateTextEditText),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                3),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("beach day"), closeSoftKeyboard());

        ViewInteraction materialRadioButton = onView(
                allOf(withId(R.id.createEntrySharedRadioButton), withText("Shared (only people you choose can see and edit this entry)"),
                        childAtPosition(
                                withId(R.id.createEntryRadioGroup),
                                1),
                        isDisplayed()));
        materialRadioButton.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.entryCreateAddContributorEditText),
                        childAtPosition(
                                withId(R.id.entryCreateAddContributorsExpandableLayout),
                                0),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText(testUsername), closeSoftKeyboard());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.createEntryPostButton), withText("Post"),
                        childAtPosition(
                                withId(R.id.entryCreateScrollViewLinearLayout),
                                6),
                        isDisplayed()));
        materialButton5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.listviewRecyclerView), isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.entryTitle), withText("shared entry"), isDisplayed()));
        textView.check(matches(withText("shared entry")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.entryAuthorTextView), withText(testUsername2+", "+testUsername), isDisplayed()));
        textView2.check(matches(withText(testUsername2+", "+testUsername)));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.entryTimestampTextView), withText("just now"), isDisplayed()));
        textView3.check(matches(withText("just now")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.entryTextTextView), withText("beach day"), isDisplayed()));
        textView4.check(matches(withText("beach day")));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction overflowMenuButton2 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                withClassName(is("androidx.appcompat.widget.ActionMenuView")),
                                1),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialTextView2 = onView(
                allOf(withId(androidx.appcompat.R.id.title), withText("Logout"),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.loginButton), withText("Log In"),
                        childAtPosition(
                                withId(R.id.loginButtonExpandableLayout),
                                0),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.usernameEditTextLogin),
                        childAtPosition(
                                withId(R.id.loginExpandableLayout),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.usernameEditTextLogin),
                        childAtPosition(
                                withId(R.id.loginExpandableLayout),
                                1),
                        isDisplayed()));
        appCompatEditText10.perform(replaceText(testUsername), closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.passwordEditTextLogin),
                        childAtPosition(
                                withId(R.id.loginExpandableLayout),
                                2),
                        isDisplayed()));
        appCompatEditText11.perform(replaceText(testPassword), closeSoftKeyboard());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.loginGoButton), withText("Log In"),
                        childAtPosition(
                                withId(R.id.loginExpandableLayout),
                                3),
                        isDisplayed()));
        materialButton7.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialButton8 = onView(
                allOf(withId(R.id.homeSharedButton), withText("Shared"),
                        childAtPosition(
                                withId(R.id.homeVisibilityLinearLayout),
                                1),
                        isDisplayed()));
        materialButton8.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.entryTitle), withText("shared entry"), isDisplayed()));
        textView5.check(matches(withText("shared entry")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.entryAuthorTextView), withText(testUsername2+", "+testUsername), isDisplayed()));
        textView6.check(matches(withText(testUsername2+", "+testUsername)));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.entryTimestampTextView), withText("just now"), isDisplayed()));
        textView7.check(matches(withText("just now")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.entryTextTextView), withText("beach day"), isDisplayed()));
        textView8.check(matches(withText("beach day")));
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
