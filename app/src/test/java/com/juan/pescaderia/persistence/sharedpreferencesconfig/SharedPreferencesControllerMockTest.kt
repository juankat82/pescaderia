package com.juan.pescaderia.persistence.sharedpreferencesconfig

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.delay
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.lang.AssertionError

internal class SharedPreferencesControllerMockTest {

    private lateinit var editor:SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    @BeforeEach
    fun init() {
        sharedPreferences = mock()
        editor = mock()
    }
    @Test
    fun setLoadingUsersScreenValue() {
        val isLoadingScreenUser = true
        editor.putBoolean("is_loading_users",isLoadingScreenUser)
        editor.commit()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putBoolean("is_loading_users",true)
            verify(editor).commit()
        }
    }

    @Test
    fun getLoadingUsersScreenValue() {
        whenever(sharedPreferences.getBoolean("loading_users_screen",false)).thenReturn(true)
        val isLoadingUsersScreen = sharedPreferences.getBoolean("loading_users_screen",false)
        verify(sharedPreferences).getBoolean("loading_users_screen",false)
        Assert.assertTrue(isLoadingUsersScreen)
    }

    @Test
    fun setLoadingOrdersScreenValue() {
        val isLoadingOrderUser = true
        editor.putBoolean("is_loading_orders",isLoadingOrderUser)
        editor.commit()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putBoolean("is_loading_orders",true)
            verify(editor).commit()
        }
    }

    @Test
    fun getLoadingOrdersScreenValue() {
        whenever(sharedPreferences.getBoolean("is_loading_orders",false)).thenReturn(true)
        val isLoadingOrdersScreen = sharedPreferences.getBoolean("is_loading_orders",false)
        verify(sharedPreferences).getBoolean("is_loading_orders",false)
        Assert.assertTrue(isLoadingOrdersScreen)
    }

    @Test
    fun setLoadingProductsScreenValue() {
        val isLoadingProductUser = true
        editor.putBoolean("is_loading",isLoadingProductUser)
        editor.commit()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putBoolean("is_loading",true)
            verify(editor).commit()
        }
    }

    @Test
    fun getLoadingProductsScreenValue() {
        whenever(sharedPreferences.getBoolean("is_loading",true)).thenReturn(false)
        val isLoadingProductsScreen = sharedPreferences.getBoolean("is_loading",true)
        verify(sharedPreferences).getBoolean("is_loading",true)
        Assert.assertFalse(isLoadingProductsScreen)
    }

    @Test
    fun setAllowNotifications() {
        val isAllowedNotifications = true
        editor.putBoolean("notifications_are_allowed",isAllowedNotifications)
        editor.apply()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putBoolean("notifications_are_allowed", isAllowedNotifications)
            verify(editor).apply()
        }
    }

    @Test
    fun isNotificationsAllowed() {
        whenever(sharedPreferences.getBoolean("notifications_are_allowed",true)).thenReturn(false)
        val notifEnabled = sharedPreferences.getBoolean("notifications_enabled",true)
        verify(sharedPreferences).getBoolean("notifications_enabled",true)
        Assert.assertFalse(notifEnabled)
    }

    @Test
    fun getLatestUpdateTime() {

        whenever(sharedPreferences.getLong("backup_moment",0L)).thenReturn(100L)
        val lastUpdate = sharedPreferences.getLong("backup_moment",0L)
        verify(sharedPreferences).getLong("backup_moment",0L)
        Assert.assertEquals(lastUpdate,100L)
    }

    @Test
    fun setLastDatabaseUpdateDate() {
        val now = 1L
        editor.putLong("backup_moment",now)
        editor.apply()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putLong("backup_moment",now)
            verify(editor).apply()
        }
    }

    @Test
    fun resetUserData() {
        val mockUser:User = mock()
        val gsonObject:Gson = mock()
        whenever(gsonObject.toJson(mockUser)).thenReturn("user")
        val mockUserJson = gsonObject.toJson(mockUser)
        editor.putString("user_json",mockUserJson)
        editor.apply()
        Thread.sleep(100L)
        verify(editor).putString("user_json",mockUserJson)
        verify(editor).apply()
        Assert.assertEquals(mockUserJson,"user")

    }

    @Test
    fun getUserData() {
        whenever(sharedPreferences.getString("user_json","")).thenReturn("0000")
        val userData = sharedPreferences.getString("user_json","")
        verify(sharedPreferences).getString("user_json","")
        Assert.assertEquals(userData,"0000")
    }

    @Test
    fun setUserData() {
        val jsonUser = ""
        editor.putString("user_json",jsonUser)
        editor.apply()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putString("user_json",jsonUser)
            verify(editor).apply()
        }
    }

    @Test
    fun setDraftOrder() {
        val jsonOrderDraft = ""
        editor.putString("json_order_list",jsonOrderDraft)
        editor.apply()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putString("json_order_list",jsonOrderDraft)
            verify(editor).apply()
        }
    }

    @Test
    fun getDraftOrder() {
        var mockOrderList = listOf<Order>()
        val order:Order = mock()
        whenever(sharedPreferences.getString("json_order_list","")).thenReturn("json-code")
        val json = sharedPreferences.getString("json_order_list","")
        verify(sharedPreferences).getString("json_order_list","")

        Assert.assertEquals(json,"json-code")
        mockOrderList = listOf(order,order,order)
        Assert.assertTrue(mockOrderList.size == 3)
    }

    @Test
    fun setSentFlag() {
        val isOrderSent = true
        editor.putBoolean("order_was_sent",isOrderSent)
        editor.apply()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putBoolean("order_was_sent",isOrderSent)
            verify(editor).apply()
        }
    }

    @Test
    fun getSentFlag() {
        //verifies execution
        val value = sharedPreferences.getBoolean("order_was_sent",true)
        Assert.assertFalse(value)
        verify(sharedPreferences).getBoolean("order_was_sent",true)
        //verifies it returns a defined value
        whenever(sharedPreferences.getBoolean(any(),any())).thenReturn(true)
        val revalue = sharedPreferences.getBoolean("order_was_sent",true)
        Assert.assertTrue(sharedPreferences.getBoolean("order_was_sent",true))
    }

    @Test
    fun setNewOrderId() {
        val orderId = "111"
        editor.putString("order_id",orderId)
        editor.apply()
        Thread.sleep(100L)

        inOrder(editor) {
            verify(editor).putString("order_id",orderId)
            verify(editor).apply()
        }
    }

    @Test
    fun getNewOrderId() {
        whenever(sharedPreferences.getString("order_id","")).thenReturn("0000")
        val orderId = sharedPreferences.getString("order_id","")
        verify(sharedPreferences).getString("order_id","")
        Assert.assertEquals(orderId,"0000")
    }
}