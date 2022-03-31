package com.juan.pescaderia.fragments

import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.MainActivity
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.LastOrderRecyclerViewAdapter
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReadLastOrderFragmentUITest {

    //WE MUST HAVE PERFORMED AND SENT AN ORDER THROUGH FOR THIS TEST TO WORK, OTHERWISE THE DATA WILL BE EMPTY
    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    private lateinit var sharedPreferences: SharedPreferencesController
    private val defaultUser = User("","-1","","","","","","","","","", true)
    private val idlingResource = SimpleIdlingResource()
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun `set_up_tests` () {
        EventBus.getDefault().register(this)
        IdlingRegistry.getInstance().register(idlingResource)
        sharedPreferences = SharedPreferencesController.getPreferencesInstance(appContext)!!
        sharedPreferences.setUserData(defaultUser)
        with(sharedPreferences) {
            val user = this.getUserData()
            if (user.email.equals("") && user.password.equals("")) {
                sharedPreferences.setUserData(User(...))
            }
        }
    }

    @Test
    fun `test_dont_order_again`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(
                DrawerActions.open())
        onView(ViewMatchers.withText("Mi Ultimo Pedido")).perform(ViewActions.click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(3000)
        onView(withId(R.id.dont_order_again_button)).perform(click())
        Thread.sleep(2000)
    }

    @Test
    fun `test_order_again`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(
                DrawerActions.open()
            )
        onView(ViewMatchers.withText("Mi Ultimo Pedido")).perform(ViewActions.click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(3000)
        onView(withId(R.id.order_again_button)).perform(click())
        onView(withText("NO")).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.order_again_button)).perform(click())
        onView(withText("SI")).perform(click())
        Thread.sleep(2000)
        //PROVIDER WONT ALLOW SENDING ORDERS THO. IT WORKS BUT ORDER WONT BE PUT THROUGH
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
