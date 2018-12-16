package com.hogent.mindfulness


import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import org.junit.*
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest {
    lateinit var prefs: SharedPreferences
    lateinit var userView: UserViewModel

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        resetData()
    }
//
//    @BeforeClass
//    fun setUpClass() {
//        resetData()
//    }

    @Test
    fun containsEmailInput() {
        onView(withId(R.id.email)).check(matches(isDisplayed()))
    }

    @Test
    fun containsPasswordInput() {
        onView(withId(R.id.login_password)).check(matches(isDisplayed()))
    }

    @Test
    fun containsLoginButton() {
        onView(withId(R.id.email_sign_in_button)).check(matches(isDisplayed()))
    }

    @Test
    fun containsRegisterButton() {
        onView(withId(R.id.login_form_register_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun containsForgotPasswordButton() {
        onView(withId(R.id.fragment_login_forgotbtn)).check(matches(isDisplayed()))
    }

    @Test
    fun canTypeEmail() {
        onView(withId(R.id.email)).perform(typeText("pacotherealdog@woef.com"), closeSoftKeyboard())
        onView(withId(R.id.email)).check(matches(withText("pacotherealdog@woef.com")))
    }

    @Test
    fun canTypePassword() {
        onView(withId(R.id.login_password)).perform(typeText("logmein"), closeSoftKeyboard())
        onView(withId(R.id.login_password)).check(matches(withText("logmein")))
    }

    @Test
    fun successfulLoginWithGroupShouldOpenMainScreen() {
        login()
        onView(withId(R.id.Progress_text)).check(matches(isDisplayed()))
    }

    @Test
    fun registerButtonShouldGoToRegisterScreen() {
        onView(withId(R.id.login_form_register_btn)).perform(scrollTo()).perform(click())
        onView(withId(R.id.textView2)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        resetData()
    }
//
//    @AfterClass
//    fun tearDownClass() {
//        resetData()
//    }

    private fun resetData() {
        userView = ViewModelProviders.of(mActivityTestRule.activity).get(UserViewModel::class.java)
        userView.userRepo.nukeUsers()

        prefs = mActivityTestRule.activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
        prefs.edit().clear()

    }

    private fun login() {
        onView(withId(R.id.email)).perform(typeText("pacotherealdog@woef.com"), closeSoftKeyboard())
        onView(withId(R.id.login_password)).perform(typeText("logmein"), closeSoftKeyboard())
        onView(withId(R.id.email_sign_in_button)).perform(click())
    }
}
