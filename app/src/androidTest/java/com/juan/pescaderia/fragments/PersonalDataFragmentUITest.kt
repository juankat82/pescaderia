package com.juan.pescaderia.fragments

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
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
import org.hamcrest.`object`.HasToString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonalDataFragmentUITest {

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
    fun `change_my_data_test`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open())
        onView(withText("Mis Datos")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(1500)

        onView(withId(R.id.cambia_nombre_edittext)).perform(clearText()).perform(typeText("Francisco"))
        onView(withId(R.id.cambia_apellido_edittext)).perform(clearText()).perform(typeText("Diaz Prado"))
        onView(withId(R.id.cambia_direccion_edittext)).perform(clearText()).perform(typeText("Avd Mediterraneo 147, 29730, Rincon de la Victoria Malaga"))
        onView(withId(R.id.cambia_phone_edittext)).perform(clearText()).perform(typeText("34952402135"))
        onView(withId(R.id.cambia_email_edittext)).perform(clearText()).perform(typeText("pescaderiavictorianarincon@gmail.com"))
        onView(withId(R.id.cambia_password_edittext)).perform(clearText()).perform(typeText("abcdefg"))
        onView(withId(R.id.cambia_repite_password_edittext)).perform(clearText()).perform(typeText("abcdefg"))
        closeSoftKeyboard()
        onView(withId(R.id.cambia_pregunta_spinner)).perform(click())
        Espresso.onData(HasToString.hasToString(Matchers.startsWith("Apellido de mi madre"))).perform(click())
        onView(withId(R.id.cambia_recover_answer_edittext)).perform(clearText()).perform(typeText("Prado"))
        Thread.sleep(1500)
        closeSoftKeyboard()
        onView(withId(R.id.boton_cambiar_datos)).perform(click())
        onView(withText("NO")).perform(click())
        onView(withId(R.id.boton_cambiar_datos)).perform(click())
        onView(withText("SI")).perform(click())//This will perform changes in production database
        Thread.sleep(3000)

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
