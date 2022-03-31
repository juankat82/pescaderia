package com.juan.pescaderia.fragments

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
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
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.hamcrest.Matchers
import org.hamcrest.Matchers.startsWith
import org.hamcrest.`object`.HasToString
import org.hamcrest.`object`.HasToString.hasToString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern

@RunWith(AndroidJUnit4::class)
class AdminUsersFragmentUITest {

    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val sharedPreferences = SharedPreferencesController.getPreferencesInstance(appContext)
    private val defaultUser = User("","-1","","","","","","","","","", true)
    private val idlingResource = SimpleIdlingResource()

    @Before
    fun `set_up_tests` () {
        EventBus.getDefault().register(this)
        IdlingRegistry.getInstance().register(idlingResource)
        sharedPreferences!!.setUserData(defaultUser)
        with(sharedPreferences) {
            val user = this.getUserData()
            if (user.email.equals("") && user.password.equals("")) {
                sharedPreferences.setUserData(
                    User(
                        ...
                    )
                )
            }
        }
    }

    @Test
    fun `test_usuario_modifica_correctamente`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Usuarios")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        Thread.sleep(3000)
        onView(withId(R.id.users_spinner)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.update_user_button)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.users_spinner)).perform(click())

        onData(HasToString.hasToString(startsWith("C-00000000--Francisco Diaz Prado"))).perform(click())
        Thread.sleep(5000)

        onView(withId(R.id.cif_change)).perform(click()).perform(typeText("C-00000001"))
        closeSoftKeyboard()
        onView(withId(R.id.update_user_button)).perform(click())
        Thread.sleep(5000L)
    }

    @Test
    fun `test_reloading_users_and_pick`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Usuarios")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        Thread.sleep(3000)
        onView(withId(R.id.admin_users_reload)).perform(click())
        onView(withId(R.id.users_spinner)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.update_user_button)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.users_spinner)).perform(click())

        onData(HasToString.hasToString(startsWith("C-00000001--Francisco Diaz Prado"))).perform(click())
        Thread.sleep(5000)
    }

    @Test
    fun `test_erase_one`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Usuarios")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        Thread.sleep(3000)
        onView(withId(R.id.users_spinner)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.update_user_button)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.users_spinner)).perform(click())

        onData(HasToString.hasToString(startsWith("C-00000001--Francisco Diaz Prado"))).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.remove_user_button)).perform(click())
        onView(withText("NO")).perform(click())
        Thread.sleep(2000)
    }

    @Test
    fun `test_erase_all`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Usuarios")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        Thread.sleep(3000)
        onView(withId(R.id.users_spinner)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.update_user_button)).check(matches(ViewMatchers.isDisplayed()))
        Thread.sleep(3000)
        closeSoftKeyboard()
        onView(withId(R.id.erase_all_users)).perform(click())
        onView(withText("NO")).perform(click())
        Thread.sleep(1000)
    }

    @Test
    fun `press_return`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open()) // Open Drawer
        onView(withText("Administrar Usuarios")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        Thread.sleep(3000)
        onView(withId(R.id.cancel_changes_button)).perform(click())
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
