package com.juan.pescaderia.fragments

import android.app.DatePickerDialog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.MainActivity
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import android.view.Gravity
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.hamcrest.Matchers.startsWith
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.intent.rule.IntentsTestRule
import org.hamcrest.`object`.HasToString.hasToString
import org.junit.*
import androidx.test.espresso.Espresso.onView

import androidx.test.espresso.contrib.PickerActions

import android.widget.DatePicker
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers

import androidx.test.espresso.matcher.ViewMatchers.withClassName

import androidx.test.espresso.matcher.ViewMatchers.withParent
import org.hamcrest.Matchers
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions.setDate
import org.hamcrest.Matchers.any


@RunWith(AndroidJUnit4::class)
class AdminOrderFragmentUITest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val sharedPreferences = SharedPreferencesController.getPreferencesInstance(appContext)
    private val defaultUser = User("","-1","","","","","","","","","", true)
    private val idlingResource = SimpleIdlingResource()

    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @Before
    fun check_user_exists() {
        EventBus.getDefault().register(this)
        IdlingRegistry.getInstance().register(idlingResource)
        sharedPreferences!!.setUserData(defaultUser)
        with(sharedPreferences) {
            val user = this.getUserData()
            if (user.email.equals("") && user.password.equals("")) {
                sharedPreferences.setUserData(
                    User(...)
                )
            }
        }
    }
    @Test
    fun check_this_is_adminorderfragment() {

        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Pedidos")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        onView(withId(R.id.users_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.buscar_button)).check(matches(isDisplayed()))
        onView(withId(R.id.users_spinner)).perform(click())

        onData(hasToString(startsWith("C-00000000--Francisco Diaz Prado"))).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.buscar_button)).perform(click())
        Thread.sleep(5000L)
        onView(withId(R.id.orders_view_pager)).perform(swipeLeft())
        Thread.sleep(2000L)
    }

    @Test
    fun check_all_dates_adminorderfragment() {

        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Pedidos")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        onView(withId(R.id.users_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.buscar_button)).check(matches(isDisplayed()))
        onView(withId(R.id.users_spinner)).perform(click())

        onData(hasToString(startsWith("C-00000000--Francisco Diaz Prado"))).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.every_order_button)).perform(click())
        onView(withId(R.id.buscar_button)).perform(click())
        Thread.sleep(4000L)
        onView(withId(R.id.orders_view_pager)).perform(swipeLeft())
        Thread.sleep(5000L)
    }

    @Test
    fun check_one_date_adminorderfragment() {

        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Pedidos")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        onView(withId(R.id.users_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.buscar_button)).check(matches(isDisplayed()))
        onView(withId(R.id.users_spinner)).perform(click())

        onData(hasToString(startsWith("C-00000000--Francisco Diaz Prado"))).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.calendar_button)).perform(click())

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2021, 5, 15))
        Thread.sleep(3000)
        onView(withText("OK")).perform(click())
        onView(withId(R.id.buscar_button)).perform(click())
        Thread.sleep(1000)

        onView(withId(R.id.buscar_button)).perform(click())
        Thread.sleep(4000L)
        onView(withId(R.id.orders_view_pager)).perform(swipeLeft())
        Thread.sleep(5000L)
    }

    @After
    fun close_event_bus() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        EventBus.getDefault().unregister(this)
    }
    @Subscribe
    fun onEvent(idlingEntity: IdlingEntity) {
        idlingResource.incrementBy(idlingEntity.incrementValue)
    }
}
