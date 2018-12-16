package com.hogent.mindfulness


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.login_form_register_btn),
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

        val editText = onView(
            allOf(
                withId(R.id.register_email),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        editText.check(matches(isDisplayed()))

        val editText2 = onView(
            allOf(
                withId(R.id.register_firstname),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        editText2.check(matches(isDisplayed()))

        val editText3 = onView(
            allOf(
                withId(R.id.register_lastname),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        editText3.check(matches(isDisplayed()))

        val editText4 = onView(
            allOf(
                withId(R.id.edit_register_password),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        editText4.check(matches(isDisplayed()))

        val editText5 = onView(
            allOf(
                withId(R.id.edit_register_repeat_password),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        editText5.check(matches(isDisplayed()))

        val button = onView(
            allOf(
                withId(R.id.btn_register),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val button2 = onView(
            allOf(
                withId(R.id.btnBackToLogin),
                childAtPosition(
                    allOf(
                        withId(R.id.register_form),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                            0
                        )
                    ),
                    9
                ),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))
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
