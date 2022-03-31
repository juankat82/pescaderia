package com.juan.pescaderia.persistence.sharedpreferencesconfig

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User

private var context:Context? = null
private var INSTANCE: SharedPreferencesController? = null

class SharedPreferencesController {
    private var sharedPreferences:SharedPreferences? = null
    private var sharedPreferencesEditor:SharedPreferences.Editor? = null
    private val gson = Gson()
    private var isAllowNotifications = true
    private var isLoadingProductsScreen =false
    private var isLoadingOrdersScreen = false
    private var isLoadingUsersScreen = false
    private var token = ""

    companion object{
        fun getPreferencesInstance(_context: Context) : SharedPreferencesController? {
            context = _context
            if (INSTANCE == null)
                synchronized(SharedPreferencesController::class.java) {
                    INSTANCE = SharedPreferencesController()
                }
            return INSTANCE
        }
        fun getCompanyEmail() = "pescaderiavictorianarincon@gmail.com"
    }

    fun setLoadingUsersScreenValue(choice:Boolean) {
        isLoadingUsersScreen = choice
        sharedPreferences = context?.getSharedPreferences("loading_users_screen",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putBoolean("is_loading_users",isLoadingUsersScreen)?.commit()
    }

    fun getLoadingUsersScreenValue() : Boolean {
        sharedPreferences = context?.getSharedPreferences("loading_users_screen",Context.MODE_PRIVATE)
        isLoadingUsersScreen = sharedPreferences?.getBoolean("is_loading_users",false) ?: false
        return isLoadingUsersScreen
    }

    fun setLoadingOrdersScreenValue(choice:Boolean) {
        isLoadingOrdersScreen = choice
        sharedPreferences = context?.getSharedPreferences("loading_orders_screen",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putBoolean("is_loading_orders",isLoadingOrdersScreen)!!.commit()
    }

    fun getLoadingOrdersScreenValue() : Boolean {
        sharedPreferences = context?.getSharedPreferences("loading_orders_screen",Context.MODE_PRIVATE)
        isLoadingOrdersScreen = sharedPreferences?.getBoolean("is_loading_orders",false) ?: false
        return isLoadingOrdersScreen
    }

    fun setLoadingProductsScreenValue(choice:Boolean) {
        isLoadingProductsScreen = choice
        sharedPreferences = context?.getSharedPreferences("loading_screen",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putBoolean("is_loading",isLoadingProductsScreen)?.commit()
    }

    fun getLoadingProductsScreenValue() : Boolean {
        sharedPreferences = context?.getSharedPreferences("loading_screen",Context.MODE_PRIVATE)
        val isLoadingProducts = sharedPreferences?.getBoolean("is_loading",false) ?: false
        return isLoadingProducts
    }

    fun setAllowNotifications(boolean: Boolean){
        isAllowNotifications = boolean
        sharedPreferences = context?.getSharedPreferences("allow_notifications",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putBoolean("notifications_are_allowed",isAllowNotifications)?.apply()
    }

    fun isNotificationsAllowed() : Boolean {
        sharedPreferences = context?.getSharedPreferences("allow_notifications",Context.MODE_PRIVATE)
        val isAllowed = sharedPreferences!!.getBoolean("notifications_are_allowed",true)
        return isAllowed
    }

    fun getLatestUpdateTime() : Long {
        sharedPreferences = context?.getSharedPreferences("backup_time",Context.MODE_PRIVATE)
        val time = sharedPreferences!!.getLong("backup_moment",0L)
        return  time
    }
    fun setLastDatabaseUpdateDate(now: Long) : Boolean{
        sharedPreferences = context?.getSharedPreferences("backup_time",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putLong("backup_moment",now)?.apply()
        return true
    }

    fun resetUserData() {
        val user = User("","-1","","","","","","","","","", true)
        sharedPreferences = context?.getSharedPreferences("user_data",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        val userJson = gson.toJson(user)
        sharedPreferencesEditor?.putString("user_json", userJson)?.apply()
    }
    fun getUserData() : User{
        var user = User("","-1","","","","","","","","","", true)
        sharedPreferences = context?.getSharedPreferences("user_data",Context.MODE_PRIVATE)
        val userJson = sharedPreferences?.getString("user_json","")
        if (!userJson.equals(""))
            user = gson.fromJson(userJson, User::class.java)

        return user
    }


    fun setUserData(user:User) {
        sharedPreferences = context?.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        val userJson = gson.toJson(user)
        sharedPreferencesEditor?.putString("user_json", userJson)?.apply()
    }

    fun setDraftOrder(orderList:List<Order>) : Boolean {
        sharedPreferences = context?.getSharedPreferences("last_order_data", Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        val orderJson = gson.toJson(orderList)
        sharedPreferencesEditor?.putString("json_order_list",orderJson)?.apply()
        return true
    }
    fun getDraftOrder() : List<Order> {
        var orderList:List<Order> = emptyList()
        sharedPreferences = context?.getSharedPreferences("last_order_data",Context.MODE_PRIVATE)
        val jsonOrderList = sharedPreferences?.getString("json_order_list","")

        if (!jsonOrderList.equals("")) {
            val orderListType = object : TypeToken<List<Order>>() {}.type
            orderList = gson.fromJson(jsonOrderList, orderListType)
        }
        return orderList
    }

    fun setSentFlag(isOrderSent:Boolean) {
        sharedPreferences = context?.getSharedPreferences("sent_flag",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putBoolean("order_was_sent",isOrderSent)?.apply()
    }
    fun getSentFlag() : Boolean {
        sharedPreferences = context?.getSharedPreferences("sent_flag",Context.MODE_PRIVATE)
        val sentFlag = sharedPreferences?.getBoolean("order_was_sent",true)
        return sentFlag!!
    }
    fun setNewOrderId(orderId:String) {
        sharedPreferences = context?.getSharedPreferences("new_order_id",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences?.edit()
        sharedPreferencesEditor?.putString("order_id",orderId)?.apply()
    }
    fun getNewOrderId() : String {
        sharedPreferences = context?.getSharedPreferences("new_order_id",Context.MODE_PRIVATE)
        val orderId = sharedPreferences?.getString("order_id","")
        return orderId!!
    }

    fun setToken(restorePasswordToken: String) {
        token = restorePasswordToken
    }
    fun getToken() = token
}