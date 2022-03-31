package com.juan.pescaderia.persistance.sharedpreferencesconfig

import android.content.SharedPreferences
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SharedPreferencesControllerTest {

    val sharedPreferences:SharedPreferences = spy()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    val context = InstrumentationRegistry.getInstrumentation().context
    val gson = Gson()

    @Test
    fun test_get_user_data() {
        //THIS HAS TO FAIL!!!
        val user = User("1","-1","","","","","","","","","", true)
        val jsonUser = gson.toJson(user)
        doReturn("rrr").whenever(sharedPreferences).getString(any(),any())
        `when`(sharedPreferences.getString(any(), any())).thenReturn("rrr")
        Assert.assertEquals(sharedPreferences.getString("user_json",""),jsonUser)
    }

    @Test
    fun test_get_draft_order() {
        //THIS HAS TO FAIL!!!
        val orderList:List<Order> = emptyList()
        val jsonList = gson.toJson(orderList)
        doReturn("list").whenever(sharedPreferences).getString(any(),any())
        `when`(sharedPreferences.getString(any(), any())).thenReturn("list")
        Assert.assertEquals(sharedPreferences.getString("json_order_list",""),jsonList)
    }
}