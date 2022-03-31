package com.juan.pescaderia.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.fragments.AdminOrderFragment
import com.juan.pescaderia.persistence.productdatabase.ProductDatabase
import com.juan.pescaderia.persistence.productdatabase.RoomRepository
import com.juan.pescaderia.recyclerviewclasses.QueryOrdersRecyclerAdapter
import com.juan.pescaderia.retrofit.retrofit.QueryService
import kotlinx.coroutines.*
import retrofit2.await


class MyViewModel(application: Application) : AndroidViewModel(application) {

    private var productListLiveData:MutableLiveData<List<Product>> = MutableLiveData()
    private var userListLiveData: MutableLiveData<List<User>> = MutableLiveData()
    private var orderLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    private var database: ProductDatabase
    private var queryService:QueryService? = QueryService.getInstance(application.applicationContext)
    private val context:Context = application.applicationContext
    private var repo:RoomRepository = RoomRepository.getInstance(application.applicationContext)!!


    init {
        database = ProductDatabase.getDatabase(context)!!
        Handler(Looper.getMainLooper()).postDelayed( {
            userListLiveData.value = emptyList()
        },200L)

    }

    fun getProductList() = productListLiveData.value ?: emptyList()

    fun loadAllProducts(erase:Boolean) {
        productListLiveData.value = runBlocking { repo.getAllProducts() }
    }
    fun loadAllProductsRemote(erase: Boolean) {
        val products: List<Product> = emptyList()
        GlobalScope.launch  (Dispatchers.Main) { queryService!!.getAllProducts(false, null).postValue(products) }
    }

    fun getAllUsers() : List<User> {
        return userListLiveData.value ?: listOf()
    }

    fun loadUserList(_fragment: Fragment?){
             GlobalScope.launch (Dispatchers.Main) {
                 userListLiveData.value = queryService!!.retrofit.getAllUsers().await()
                 if (_fragment != null){
                     val fragment = _fragment as AdminOrderFragment
                     Handler(Looper.getMainLooper()).postDelayed({
                         Thread.sleep(1500)
                         fragment.enableViewListeners()
                     }, 200L)
                 }
             }
    }

    fun doGetOneUserById(id:String) {
        if (queryService == null)
            showAToast(R.string.query_engine_failure)
        else
        {
            GlobalScope.launch (Dispatchers.Main){
                userListLiveData.value = queryService!!.retrofit.getAllUsers().await()
                //NOW RETURN THE MATCHING ONE
            }
        }
    }

    fun getAllOrdersByCif(user:User,adapter:QueryOrdersRecyclerAdapter) {
        if (queryService == null)
            showAToast(R.string.query_engine_failure)
        else
        {
            runBlocking<Unit> {
                GlobalScope.launch (Dispatchers.Main) {
                    queryService!!.getOrdersByCustomerCif(user.cif,adapter,null)
                }
            }
        }

       orderLiveData = queryService!!.getOrderByCifLiveData()
    }

    fun getAllOrdersByCifAndDate(user:User,date:String,adapter:QueryOrdersRecyclerAdapter) {
        if (queryService == null)
            showAToast(R.string.query_engine_failure)
        else
        {
            runBlocking<Unit> {
                GlobalScope.launch (Dispatchers.Main) {
                    queryService!!.getOrdersByCifAndDate(user.cif,date,adapter,null)
                }
            }
        }
        orderLiveData = queryService!!.getOrderByCifAndDateLiveData()
    }

    private fun showAToast(stringId:Int){
        Handler(Looper.getMainLooper()).postDelayed( {
            Toast.makeText(context, context.resources.getString(stringId), Toast.LENGTH_SHORT).show()
        },200L)
    }
}

