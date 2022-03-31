package com.juan.pescaderia.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.juan.pescaderia.R
import com.juan.pescaderia.databaseupdater.RemoteDatabaseUploaderReceiver
import com.juan.pescaderia.dataclasses.Email
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.productdatabase.ProductDatabase
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.viewmodel.MyViewModel

private const val toEmailAddress:String = ""

class EmailService : IntentService("EmailService") {

    private var supportPackageManager:PackageManager? = null
    private val gson:Gson = Gson()
    private var user: User? = null
    private var orderList:List<Order>? = null
    private var email:Email? = null
    private var emailAppChooser:Intent? = null
    private var sharedPreferences:SharedPreferencesController? = null
    private var database:ProductDatabase? = null
    private val remoteUploaderReceiver = RemoteDatabaseUploaderReceiver()
    private lateinit var myViewModel: MyViewModel
    private lateinit var productList:List<Product>
    private var queryService:QueryService? = null
    private lateinit var conManager: ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null

    override fun onHandleIntent(intent: Intent?) {

        sharedPreferences = SharedPreferencesController.getPreferencesInstance(applicationContext)
        user = sharedPreferences!!.getUserData()
        orderList = sharedPreferences!!.getDraftOrder()
        supportPackageManager = applicationContext.packageManager
        queryService = QueryService.getInstance(applicationContext)
        database = ProductDatabase.getDatabase(applicationContext)
        myViewModel = MyViewModel(application)
        productList = myViewModel.getProductList()
        conManager =applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo

        if (!orderList.isNullOrEmpty()) {
            var emailBody = "" +
                        "${user!!.name} " +
                        "${user!!.surname}\n" +
                        "CIF: ${user!!.cif}\n" +
                        "TFN: ${user!!.telephone}\n" +
                        "Direccion:\n${user!!.address}\n" +
                        "Email: ${user!!.email}\n\n\n\n\n\n"+
                        "Hola Francisco, este es mi pedido para manana: \n\n"

            orderList!!.forEach {
                val product = database!!.productDao().getProductById(it.id_producto) //as INT?

                val byUnit = if(product.peso_kg) "kg" else "unidades"
                emailBody += "\n${it.nombre_producto}\t\t${it.cantidad}\t${byUnit}"
            }
            emailBody += "\nMuchas gracias.\n${user!!.name}"
            email = Email(user!!.email, toEmailAddress,"Pedido",emailBody)

            val intentEmail = Intent(Intent.ACTION_SEND)
            intentEmail.type = "message/rfc822"
            intentEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf(email?.to))
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, email?.subject)
            intentEmail.putExtra(Intent.EXTRA_TEXT, email?.emailBody)

            val sendEmailFunction = {doIfPermissionIsGranted(intentEmail)}
            netInfo = conManager.activeNetworkInfo
            isNetActive = checkNetActive()
            if (isNetActive) {
                queryService?.checkEmailForPermission(user as User,sendEmailFunction)
            }
            else
                showAToast(R.string.no_internet_disponible)
        }
        else
            showAToast(R.string.no_puedo_email)

        sharedPreferences?.setSentFlag(true)
        addToRemoteOrderDatabase(user,orderList)
    }

    private fun doIfPermissionIsGranted(intentEmail: Intent) {
        if ((user as User).approvedByAdmin) {
            val acts = supportPackageManager!!.queryIntentActivities(intentEmail, PackageManager.MATCH_DEFAULT_ONLY)


            if (acts.size > 0) {
                emailAppChooser = Intent.createChooser(intentEmail, "Gmail")
                emailAppChooser!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(emailAppChooser)
            } else
                showAToast(R.string.imposible_mandar_pedido)
        }
        else
            showAToast(R.string.no_permitido_pedidos)
    }
    fun addToRemoteOrderDatabase(_user:User?, _productList:List<Order>?) {
        val filter = IntentFilter("update_databases")
        registerReceiver(remoteUploaderReceiver,filter)
        val remoteDatabaseUpdateIntent = Intent(applicationContext,RemoteDatabaseUploaderReceiver::class.java)
        val bundle = Bundle()
        bundle.putString("USER",gson.toJson(_user))
        bundle.putString("ORDERLIST",gson.toJson(_productList))
        remoteDatabaseUpdateIntent.putExtra("DATA_BUNDLE",bundle)
        sendBroadcast(remoteDatabaseUpdateIntent)
    }

    override fun onDestroy() {
        unregisterReceiver(remoteUploaderReceiver)
        ProductDatabase.destroyDatabase()
        super.onDestroy()
    }

    private fun showAToast(stringId:Int)
    {
        Handler(Looper.getMainLooper()).postDelayed( {
            Toast.makeText(this,resources.getString(stringId),Toast.LENGTH_SHORT).show()
        },200L)
    }

    private fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }
}
