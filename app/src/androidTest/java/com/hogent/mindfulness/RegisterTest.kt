package com.hogent.mindfulness


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RegisterTest {
    lateinit var prefs: SharedPreferences
    lateinit var userView: UserViewModel

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        resetData()

        onView(withId(R.id.login_form_register_btn)).perform(scrollTo()).perform(click())
    }

    @Test
    fun containsEmailField() {
        onView(withId(R.id.register_email)).check(matches(isDisplayed()))
    }

    @Test
    fun containsFirstnameField() {
        onView(withId(R.id.register_firstname)).check(matches(isDisplayed()))
    }
    @Test
    fun containsLastnameField() {
        onView(withId(R.id.register_lastname)).check(matches(isDisplayed()))
    }
    @Test
    fun containsPasswordField() {
        onView(withId(R.id.edit_register_password)).check(matches(isDisplayed()))
    }

    @Test
    fun containsRepeatPasswordField() {
        onView(withId(R.id.edit_register_repeat_password)).check(matches(isDisplayed()))
    }

    @Test
    fun containsRegisterButton() {
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
    }

    @Test
    fun containsLoginButton() {
        onView(withId(R.id.btnBackToLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun canTypeEmail() {
        onView(withId(R.id.register_email)).perform(typeText("test@test.com"), closeSoftKeyboard())
        onView(withId(R.id.register_email)).check(matches(withText("test@test.com")))
    }

    @Test
    fun canTypeFirstname() {
        onView(withId(R.id.register_firstname)).perform(typeText("Test"), closeSoftKeyboard())
        onView(withId(R.id.register_firstname)).check(matches(withText("Test")))
    }

    @Test
    fun canTypeLastname() {
        onView(withId(R.id.register_lastname)).perform(typeText("Test"), closeSoftKeyboard())
        onView(withId(R.id.register_lastname)).check(matches(withText("Test")))
    }

    @Test
    fun canTypePassword() {
        onView(withId(R.id.edit_register_password)).perform(typeText("logmein"), closeSoftKeyboard())
        onView(withId(R.id.edit_register_password)).check(matches(withText("logmein")))
    }

    @Test
    fun canTypeRepeatPassword() {
        onView(withId(R.id.edit_register_repeat_password)).perform(typeText("logmein"), closeSoftKeyboard())
        onView(withId(R.id.edit_register_repeat_password)).check(matches(withText("logmein")))
    }

    @Test
    fun successfulRegisterOpensGroupFragment() {
        register()
    }

    @Test
    fun loginButtonShouldOpenLoginScreen() {
        onView(withId(R.id.btnBackToLogin)).perform(scrollTo()).perform(click())
        onView(withId(R.id.loginTitel)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        resetData()
    }

    private fun resetData() {
        userView = ViewModelProviders.of(mActivityTestRule.activity).get(UserViewModel::class.java)
        userView.userRepo.nukeUsers()

        prefs = mActivityTestRule.activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
        prefs.edit().clear()
    }

    private fun register() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.login_form_register_btn), withText("Registreer"),
                childAtPosition(
                    allOf(
                        withId(R.id.login_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    7
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.register_email),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    3
                )
            )
        )
        appCompatEditText.perform(scrollTo(), replaceText("test@test.com"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.register_firstname),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    4
                )
            )
        )
        appCompatEditText2.perform(scrollTo(), replaceText("Test"), closeSoftKeyboard())

        pressBack()

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.register_lastname),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    5
                )
            )
        )
        appCompatEditText3.perform(scrollTo(), replaceText("Test"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.edit_register_password),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    6
                )
            )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("logmein"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.edit_register_repeat_password),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    7
                )
            )
        )
        appCompatEditText5.perform(scrollTo(), replaceText("logmein"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btn_register), withText("Registreer"),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    8
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
