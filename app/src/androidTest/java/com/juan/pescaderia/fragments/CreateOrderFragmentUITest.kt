package com.juan.pescaderia.fragments

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.juan.pescaderia.MainActivity
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.NewOrderRecyclerAdapter
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateOrderFragmentUITest {

    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    //    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var sharedPreferences: SharedPreferencesController
    private val defaultUser = User("","-1","","","","","","","","","", true)
    private val idlingResource = SimpleIdlingResource()
    private lateinit var appContext: Context
    private var list = listOf(Order(...))

    @Before
    fun `set_up_tests` () {
        EventBus.getDefault().register(this)
        IdlingRegistry.getInstance().register(idlingResource)
        appContext = intentsRule.activity.applicationContext
        sharedPreferences = SharedPreferencesController.getPreferencesInstance(appContext)!!
        sharedPreferences.setUserData(defaultUser)
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
    fun `test_create_order`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(
            DrawerActions.open()) // Open Drawer
        onView(withText("Crear Pedido Nuevo")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

       onView(withId(R.id.new_order_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<NewOrderRecyclerAdapter.ProductHolder>(0,object : ViewAction{
           override fun getConstraints(): Matcher<View> {
               TODO("Not yet implemented")
           }

           override fun getDescription() = "Create Order Fragment Order Product"


           override fun perform(uiController: UiController?, view: View?) {
               val amountEditText = view?.findViewById<AppCompatEditText>(R.id.cantidad_deseada_edittext)
               amountEditText?.setText("1")
               val button = view?.findViewById<ImageButton>(R.id.add_to_order_button)
               button?.performClick()
           }
       }))

        onView(withId(R.id.anadir_a_cesta_button)).perform((click()))

        Thread.sleep(2000)
    }

    @Test
    fun `test_create_order_and_test_erase_button`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(
                DrawerActions.open()) // Open Drawer
        onView(withText("Crear Pedido Nuevo")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        onView(withId(R.id.new_order_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<NewOrderRecyclerAdapter.ProductHolder>(0,object : ViewAction{
            override fun getConstraints(): Matcher<View> {
                TODO("Not yet implemented")
            }

            override fun getDescription() = "Create Order Fragment Order Product"

            override fun perform(uiController: UiController?, view: View?) {
                val amountEditText = view?.findViewById<AppCompatEditText>(R.id.cantidad_deseada_edittext)
                amountEditText?.setText("1")
                val button = view?.findViewById<ImageButton>(R.id.add_to_order_button)
                button?.performClick()
            }
        }))
        Thread.sleep(2000)


        onView(withId(R.id.list_ordenes)).perform(RecyclerViewActions.actionOnItemAtPosition<NewOrderRecyclerAdapter.ProductHolder>(0,object : ViewAction{
            override fun getConstraints(): Matcher<View> {
                TODO("Not yet implemented")
            }
            override fun getDescription() = "Create Order Fragment Order Product"

            override fun perform(uiController: UiController?, view: View?) {
                val button = view?.findViewById<Button>(R.id.remove_item_button)
                button?.performClick()
            }
        }))

        Thread.sleep(2000)
    }

    @Test
    fun `cancel_order`() {
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))).perform(
                DrawerActions.open()) // Open Drawer
        onView(withText("Crear Pedido Nuevo")).perform(click())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())

        onView(withId(R.id.cancelar_pedido_button)).perform(click())
    }
    //////////////////TEST/////////////////
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
