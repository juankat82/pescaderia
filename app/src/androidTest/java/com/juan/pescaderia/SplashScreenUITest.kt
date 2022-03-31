package com.juan.pescaderia


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.dataclasses.User
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.juan.pescaderia.fragments.AdminOrderFragment
import com.juan.pescaderia.fragments.LoginFragment
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import org.junit.After

import org.junit.Rule




@RunWith(AndroidJUnit4::class)
class SplashScreenUITest {


    //THIS WORKS STRAIGHT ON THE PRODUCTION DATABASE. BEST DOING WITH MOCKWEBSERVER

    @Before
    fun onSplashScreenCreation() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = SharedPreferencesController.getPreferencesInstance(appContext)
        val defaultUser = User("","-1","","","","","","","","","", true)

        with(sharedPreferences) {
            sharedPreferences!!.setUserData(defaultUser)
        }

        ActivityScenario.launch(SplashScreen::class.java)
        onView(withId(R.id.intro_image_view)).check(matches(isDisplayed()))

        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(2000)
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))
        onView(withId(R.id.register_button)).check(matches(isDisplayed()))
    }


    @Test
    fun test_login_button_click_listener () {
        onView(withId(R.id.login_button)).perform(click())
        Espresso.pressBack()      
    }

    @Test
    fun test_register_button_click_listener () {
        onView(withId(R.id.register_button)).perform(click())
        Espresso.pressBack()
    }

    //Assumes theres no login performed at the time of testing the app
    @Test
    fun test_login_fragment_works () {
        //checks login button works (it does now)
        onView(withId(R.id.login_button)).perform(click())
        onView(withText(R.string.haz_login)).check(matches(isDisplayed()))

        onView(withId(R.id.name_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.password_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.cancelar_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.login_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withText(R.string.olvide_contrasena_string)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        onView(withId(R.id.name_edittext)).perform(typeText("jcnexus582@gmail.com")) //tttttt
        onView(withId(R.id.password_edittext)).perform(typeText("ccc"), closeSoftKeyboard())

        onView(withId(R.id.login_button)).perform(click())
        Thread.sleep(2000)
    }

    //Assumes theres no login performed at the time of testing the app
    @Test
    fun test_cancel_button_works_in_login_fragment() {
        //check cancel button works in LoginFragment
        onView(withId(R.id.login_button)).perform(click())
        onView(withText(R.string.haz_login)).check(matches(isDisplayed()))

        onView(withId(R.id.name_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.password_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.cancelar_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.login_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withText(R.string.olvide_contrasena_string)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        onView(withId(R.id.name_edittext)).perform(typeText("jcnexus582@gmail.com")) //tttttt
        onView(withId(R.id.password_edittext)).perform(typeText("ccc"), closeSoftKeyboard())

        onView(withId(R.id.cancelar_button)).perform(click())
    }

    //Assumes theres no login performed at the time of testing the app
    @Test
    fun test_recover_password_functionality_in_login_fragment() {
        onView(withId(R.id.login_button)).perform(click())
        onView(withText(R.string.haz_login)).check(matches(isDisplayed()))

        onView(withId(R.id.name_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.password_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.cancelar_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.login_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withText(R.string.olvide_contrasena_string)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.olvido_password_text_view)).perform(click())

        onView(withId(R.id.name_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.cif_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.boton_aceptar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.name_edittext)).perform(typeText("jcnexus582@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.cif_edittext)).perform(typeText("C-11111122"), closeSoftKeyboard())
        onView(withId(R.id.boton_aceptar)).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.pregunta_recuperacion_textView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.respuesta_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.boton_aceptar_respuesta)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.respuesta_edittext)).perform(typeText("coca"), closeSoftKeyboard())
        onView(withId(R.id.boton_aceptar_respuesta)).perform(click())
        Thread.sleep(2000)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        var sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(appContext)
        val passToken = sharedPreferencesController?.getToken()
        onView(withId(R.id.escriba_codigo_textview)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.escriba_codigo_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.aceptar_codigo_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.escriba_codigo_edittext)).perform(typeText(passToken), closeSoftKeyboard())
        onView(withId(R.id.aceptar_codigo_button)).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.escriba_nuevo_password_textview)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.escriba_nuevo_password_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.repita_nuevo_password_textview)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.repita_nuevo_password_edittext)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.boton_cambio_contrasena)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        val newPass = "xxx"
        onView(withId(R.id.escriba_nuevo_password_edittext)).perform(typeText(newPass), closeSoftKeyboard())
        onView(withId(R.id.repita_nuevo_password_edittext)).perform(typeText(newPass), closeSoftKeyboard())
        onView(withId(R.id.boton_cambio_contrasena)).perform(click())
        Thread.sleep(2000)
    }

    @Test
    fun test_support_action_bar_works() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).perform(click())
        Thread.sleep(1200)
        onView(withId(R.id.toolbar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

    }

    @Test
    fun test_drawer_layout_works() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer
    }
}
