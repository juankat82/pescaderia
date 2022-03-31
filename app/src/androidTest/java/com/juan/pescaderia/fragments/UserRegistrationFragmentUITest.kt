package com.juan.pescaderia.fragments

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
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
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.hamcrest.`object`.HasToString.hasToString
import org.hamcrest.core.StringStartsWith.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserRegistrationFragmentUITest {

    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    private lateinit var sharedPreferences: SharedPreferencesController
    private val defaultUser = User("","-1","","","","","","","","","", true)
    private val idlingResource = SimpleIdlingResource()
    private var list = listOf(Order("602af1a6b146b21f0002873a", "15_02_2021", "1613427098303","C-00000000", "xxcccvvvbb", 3.0F, 7.0F, 21.0F, "7"))
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
                sharedPreferences.setUserData(User(..))
            }
        }
    }

    @Test
    fun `register_new_user_test`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(
            DrawerActions.open())
        onView(withText("Registrar Usuario")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(1500)
        onView(withId(R.id.cif_usuario)).perform(typeText("C-11223344"))
        closeSoftKeyboard()
        onView(withId(R.id.nombre_usuario)).perform(typeText("Pedro"))
        closeSoftKeyboard()
        onView(withId(R.id.apellidos_usuario)).perform(typeText("Pedrito Pedrez"))
        closeSoftKeyboard()
        onView(withId(R.id.direccion_usuario)).perform(typeText("C/ La Mota 25"))
        closeSoftKeyboard()
        onView(withId(R.id.telefono_usuario)).perform(typeText("273273200"))
        closeSoftKeyboard()
        onView(withId(R.id.email_usuario)).perform(typeText("paquitoruiz@plipli.com"))
        closeSoftKeyboard()
        onView(withId(R.id.contrasena_usuario)).perform(typeText("Certe_023p;"))
        closeSoftKeyboard()
        onView(withId(R.id.repite_contrasena_usuario)).perform(typeText("Certe_023p;"))
        closeSoftKeyboard()
        onView(withId(R.id.pregunta_recuperacion_contrasena_spinner)).perform(click())
        onData(hasToString(startsWith("Nombre de mi primera mascota"))).perform(click())
        Thread.sleep(1500)
        closeSoftKeyboard()
        onView(withId(R.id.respuesta_recuperacion_contrasena)).perform(typeText("Whisky"))
        closeSoftKeyboard()
        onView(withId(R.id.accept_button)).perform(click())
        Thread.sleep(2500)
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
