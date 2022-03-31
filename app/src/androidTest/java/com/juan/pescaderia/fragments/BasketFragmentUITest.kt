package com.juan.pescaderia.fragments

import android.content.Context
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.MainActivity
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.BasketRecyclerAdapter
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasketFragmentUITest {


    /////////////ASEGURARSE QUE HAY ALGO EN LA CESTA O ESTAS PRUEBAS FALLARAN!!!!!!!/////////////
    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    private lateinit var sharedPreferences:SharedPreferencesController
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
                sharedPreferences.setUserData(User("5fcd59a4f3102e310001e3d5", "0000", "C-00000000", "Francisco", "Diaz Prado", "Avd Mediterraneo 147, 29730, Rincon de la Victoria", "34952402135", "pescaderiavictorianarincon@gmail.com", "abcdefg", "Apellido de mi madre", "Prado", true))
            }
        }
    }

    @Test
    fun `start_test`() {

        //REQUIRES AN ITEM AT LEAST IN BASKET!!!!! (CAN BE MANUALLY DONE OR CALLING THE APP)
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open())
        onView(withText("Cesta")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(3000)
        onView(withId(R.id.finalizar_pedido)).perform(click())
        onView(withText("SI")).perform(click())
        //It will fail as its not allowed to send orders
        Thread.sleep(2000L)
    }

    @Test
    fun `test_no_cancelar_pedido`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open())
        onView(withText("Cesta")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(1000)
        onView(withId(R.id.cancelar_compra_button)).check(matches(isDisplayed()))
        onView(withId(R.id.cancelar_compra_button)).perform(click())
        Thread.sleep(2000)
        onView(withText("NO")).perform(click())
        Thread.sleep(2000)
    }

    @Test
    fun `test_si_cancelar_pedido`() {
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open())
        onView(withText("Cesta")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(1000)
        onView(withId(R.id.cancelar_compra_button)).check(matches(isDisplayed()))
        onView(withId(R.id.cancelar_compra_button)).perform(click())
        Thread.sleep(2000)
        onView(withText("SI")).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(DrawerActions.open())
        onView(withText("Cesta")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
        Thread.sleep(1000)
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