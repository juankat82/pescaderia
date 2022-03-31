package com.juan.pescaderia.databaseupdater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.TextMessage
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.services.SmsService

private const val PHONE_NUMBER = ""

class RemoteDatabaseUploaderReceiver : BroadcastReceiver() {

    private lateinit var context:Context
    private lateinit var user: User
    private lateinit var orderList: List<Order>
    private val gson = Gson()
    private var queryService:QueryService? = null
    private lateinit var bundle: Bundle
    private lateinit var conManager: ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null

    override fun onReceive(_context: Context?, intent: Intent?) {
        context = _context!!
        bundle = intent?.getBundleExtra("DATA_BUNDLE") ?: Bundle()
        conManager =context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        queryService = QueryService.getInstance(_context)
        if (!bundle.containsKey("USER") && !bundle.containsKey("ORDERLIST") && queryService != null)
            showAToast(R.string.invalid_update)
        else
        {
            user = gson.fromJson(bundle.getString("USER",""),User::class.java)
            val orderListType = object : TypeToken<List<Order>>() {}.type
            orderList = gson.fromJson(bundle.getString("ORDERLIST",""),orderListType)
            uploadOrder(orderList,user)

            netInfo = conManager.activeNetworkInfo
            isNetActive = checkNetActive()
            val sms = { sendSms()}
            if (isNetActive) {
                queryService?.checkForSmsPermission(user, sms)
            }
            else
                showAToast(R.string.no_internet_disponible)

        }
    }
    fun uploadOrder(orderList:List<Order>,user:User){

        netInfo = conManager.activeNetworkInfo
        isNetActive = checkNetActive()
        if (isNetActive) {
            queryService.let { it?.sendOrder(orderList, user) }
        }
        else
            showAToast(R.string.no_internet_disponible)
    }
    fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }

    fun sendSms() {
        val sms = TextMessage(user.telephone, PHONE_NUMBER,context.resources.getString(R.string.tienes_un_pedido).plus("${user.name} ${user.surname}"))
        bundle.putParcelable("sms",sms)
        val smsIntent = Intent(context, SmsService::class.java)
        smsIntent.putExtra("SmsBundle",bundle)
        context.startService(smsIntent)
    }

    fun showAToast(str:Int)
    {
        Handler(Looper.getMainLooper()).postDelayed( {
            Toast.makeText(context,context.resources.getString(str), Toast.LENGTH_SHORT).show()
        },200L)
    }
}
