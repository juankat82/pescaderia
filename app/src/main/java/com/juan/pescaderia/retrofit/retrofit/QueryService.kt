package com.juan.pescaderia.retrofit.retrofit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.fragments.AdminOrderFragment
import com.juan.pescaderia.fragments.AdminProductsFragment
import com.juan.pescaderia.fragments.AdminUsersFragment
import com.juan.pescaderia.persistence.productdatabase.RoomRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.AdminProductsRecyclerAdapter
import com.juan.pescaderia.recyclerviewclasses.QueryOrdersRecyclerAdapter
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus

private const val BASE_URL = ""
private var queryServiceInstance:QueryService? = null
private lateinit var context:Context

class QueryService {

    var retrofit: RetrofitInterface
    private var client: OkHttpClient
    private val logger = HttpLoggingInterceptor()
    private val gson = GsonBuilder().serializeNulls().create()
    private val userLiveData: MutableLiveData<User> = MutableLiveData()
    val userListLiveData: MutableLiveData<List<User>> = MutableLiveData()
    private val productLiveData: MutableLiveData<Product> = MutableLiveData()
    private val productListLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    private val orderByIdLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    private val orderByCifAndDateLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    val compositeDisposable = CompositeDisposable()
    private var eraseAswell = false
    private var database: RoomRepository? = null
    private var sharedPreferences: SharedPreferencesController? = null

    companion object {
        fun getInstance(_context: Context): QueryService? {
            context = _context
            if (queryServiceInstance == null) {
                queryServiceInstance = QueryService()
            }
            return queryServiceInstance
        }
    }

    init {
        database = RoomRepository.getInstance(context)
        client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .callTimeout(60L, TimeUnit.SECONDS)
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(RetrofitInterface::class.java)

        sharedPreferences = SharedPreferencesController.getPreferencesInstance(context)
    }

    fun getAllUsersLiveData(callback: Callback<List<User>>): List<User> {
        val userList:List<User> = emptyList()
        retrofit.getAllUsers().enqueue( object: Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.no_usuarios_definidos)
                userListLiveData.value = listOf()
            }
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    userListLiveData.value = response.body()
                }
            }
        })
        return userList
    }

    fun findUserToRecover(findUserFunction: (List<User>) -> Unit) {
        retrofit.getAllUsers().enqueue( object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    findUserFunction(response.body() as List<User>)
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.usuario_desconocido)
            }
        })
    }

    fun sendOrder(orderList: List<Order>, user: User) : Boolean{
        var isAllowed = false
        retrofit.getAllUsers().enqueue( object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    val userIndex = userList?.indexOf(user)
                    val chosenUser:User?
                    if (userIndex!! > -1)
                    {
                        chosenUser = userList[userList.indexOf(user)]
                        isAllowed = chosenUser.approvedByAdmin
                    }
                    if (isAllowed)
                        putOrderInDatabase(orderList)
                    else
                        showAToast(R.string.no_permitido_pedidos)
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.fallo_pedido)
            }
        })
        return isAllowed
    }

    fun locateAndSetUserInSystem(loginFunction: (List<User>) -> Unit, _progressBarLayout: LinearLayout, _baseLayout: ConstraintLayout) {
        retrofit.getAllUsers().enqueue( object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    Thread.sleep(1500)
                    val userList = response.body()
                    loginFunction(userList ?: emptyList())
                }
                else
                    showAToast(R.string.user_doesnt_exist)
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.user_doesnt_exist)
            }
        })
        if (_progressBarLayout.visibility == View.GONE)
            _progressBarLayout.visibility = View.VISIBLE
        if (_baseLayout.alpha == 0.5F)
            _baseLayout.alpha = 1.0F
    }

    fun checkForSmsPermission(user: User, sendSms: () -> Unit) : Boolean{
        var isAllowed = false
        retrofit.getAllUsers().enqueue( object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    val userIndex = userList?.indexOf(user)
                    val chosenUser:User?
                    if (userIndex!! > -1)
                    {
                        chosenUser = userList[userList.indexOf(user)]
                        isAllowed = chosenUser.approvedByAdmin
                    }

                    if (isAllowed)
                        sendSms()
                    else
                        showAToast(R.string.no_permitido_pedidos)
                }
                else
                    isAllowed = false
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.fallo_pedido)
            }
        })
        return isAllowed
    }

    fun checkEmailForPermission(user:User, func:() -> Unit) {
        var isAllowed = false
        retrofit.getAllUsers().enqueue( object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    val userIndex = userList?.indexOf(user)
                    val chosenUser:User?
                    if (userIndex!! > -1)
                    {
                        chosenUser = userList[userList.indexOf(user)]
                        isAllowed = chosenUser.approvedByAdmin
                    }

                    if (isAllowed)
                        func()
                    else
                        showAToast(R.string.no_permitido_pedidos)
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.fallo_pedido)
            }
        })
    }

    fun getAllUsersForSpinner(arrayAdapter: ArrayAdapter<String>, _fragment: Fragment?,fragmentChoice:Int) {
        var userList:List<User>?
        EventBus.getDefault().post(IdlingEntity(1))
        retrofit.getAllUsers().enqueue( object: Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showAToast(R.string.no_usuarios_definidos)
                userListLiveData.value = listOf()
            }
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                val listOfValues = mutableListOf<String>()
                if (response.isSuccessful)
                {
                    if (_fragment != null)
                    {
                        if (fragmentChoice == 0)
                            (_fragment as AdminOrderFragment).disableViewListeners()
                        else
                            (_fragment as AdminUsersFragment).disableViewListeners()
                    }
                    val fakeUser = User(" "," "," "," "," "," "," "," "," "," "," ",true)
                    val tempList:MutableList<User> = mutableListOf()
                    tempList.add(fakeUser)
                    tempList.addAll(response.body() as List)
                    userList = tempList
                    userList?.forEach {
                        val string = "${it.cif}--${it.name} ${it.surname}"
                        listOfValues.add(string)
                    }
                    arrayAdapter.clear()
                    arrayAdapter.addAll(listOfValues)
                    if (_fragment != null)
                    {
                        Handler(Looper.getMainLooper()).postDelayed( {
                            Thread.sleep(1500)
                            if (fragmentChoice == 0){
                                sharedPreferences!!.setLoadingProductsScreenValue(false)
                                (_fragment as AdminOrderFragment).enableViewListeners()
                            }
                            else {
                                sharedPreferences!!.setLoadingUsersScreenValue(false)
                                (_fragment as AdminUsersFragment).enableViewListeners()
                            }
                            arrayAdapter.notifyDataSetChanged()
                        },200L)
                    }
                    else
                        arrayAdapter.notifyDataSetChanged()
                }
            }
        })
        EventBus.getDefault().post(IdlingEntity(-1))
    }

    fun createUserInRemoteDatabase(user: User, requireActivity: FragmentActivity?, option: Int,_progressBarLayout:LinearLayout,_baseLayout:ConstraintLayout) {
        var success = false
          compositeDisposable.add(
            retrofit.createUser(user).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                {
                    onResponse(null, 2, R.string.usuario_creado,null,null)
                    if (option==1)
                        sharedPreferences?.setUserData(it)

                    showHideProgressBar(_progressBarLayout,_baseLayout,true,requireActivity)
                },
                {
                    onFailure(R.string.no_puedo_crear_usuario)
                    showHideProgressBar(_progressBarLayout, _baseLayout, false, requireActivity)
                }))
    }

    internal fun showHideProgressBar(_progressBarLayout: LinearLayout, _baseLayout: ConstraintLayout, success: Boolean, requireActivity: FragmentActivity?) {
        Handler(Looper.getMainLooper()).postDelayed({
            _progressBarLayout.visibility = View.GONE
            _baseLayout.alpha = 1.0F
            Thread.sleep(1500)
            if (success)
                requireActivity!!.onBackPressed()
        },200L)
    }
    fun updateUserData(userId: String, newUserData: User, _progressBarLayout: LinearLayout?, _baseLayout: ConstraintLayout?, _requireActivity: FragmentActivity?) {
        compositeDisposable.add(
            retrofit.updateUserById(userId,newUserData).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(
                    { onResponse( null, 3, R.string.exito_modificar_usuario,null,null)
                        showHideProgressBar(_progressBarLayout!!,_baseLayout!!,true,_requireActivity)},
                    { onFailure(R.string.error_modificar_usuario) }
                ))
    }

    fun deleteUserById(userId:String) {
        compositeDisposable.add(
            retrofit.deleteUserById(userId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .doOnComplete { onResponse( null,4,R.string.usuario_eliminado_exito,null,null) }
                .doOnError {onFailure(R.string.no_puedo_eliminar_usuario) }
                .subscribe())
    }

    fun deleteAllUsers(userList:List<User>) {
        var counter = 0

        val publishSubject = PublishSubject.create<User>()
            userList.forEach { retrofit.deleteAllUsers(it._id!!).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
            .doOnComplete {
                counter++
                onResponse( null,5,if (counter == userList.size) R.string.usuario_eliminado_exito else R.string.string_vacio,null,null)
            }
            .doOnError {
                counter--
                onFailure(R.string.no_puedo_eliminar_usuario)
            }.subscribe()
        }
        userList.forEach {
            publishSubject.onNext(it)
        }
    }

    fun getAllProducts(_eraseAswell: Boolean, adapter: AdminProductsRecyclerAdapter?): MutableLiveData<List<Product>> {
        eraseAswell = _eraseAswell

        compositeDisposable.add(
             retrofit.getAllProducts().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(
                    { response ->
                       if (eraseAswell && response.isNotEmpty())
                       {
                           GlobalScope.launch {
                               database!!.deleteAllProductsLocalDB(response)
                               delay(500)
                               database!!.insertListInRoomDatabase(response as List<Product>)
                           }.start()
                           eraseAswell = false
                       }
                        else
                           onResponse(response, 6, R.string.string_vacio,adapter,null)
                    },
                    { onFailure(R.string.database_not_updated) }
                ))
        return productListLiveData
    }

    fun putProductListInDatabase(product: Product, productList: MutableList<Product>, productAdminRecyclerAdapter: AdminProductsRecyclerAdapter, thisFragment: AdminProductsFragment) {
        val defList = mutableListOf<Product>()
        defList.add(product)
        defList.addAll(productList)
        compositeDisposable.add(
            retrofit.enterNewProductList(listOf(product)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({
                    sharedPreferences?.setLoadingProductsScreenValue(true)
                    thisFragment.disableViewListeners()
                    onResponse(defList, 8, R.string.producto_introducido,productAdminRecyclerAdapter,thisFragment) },
                    { onFailure( R.string.no_introduce_producto) }
                )
        )
    }

    fun deleteAProductById(_id:String) {
    compositeDisposable.add(
        retrofit.deleteProductById(_id).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnComplete { onResponse(null, 9, R.string.eliminado_exito,null,null) }
            .doOnError {onFailure(R.string.no_eliminado) }
            .subscribe())
    }

    fun getOrdersByCustomerCif(cif:String, orderCifAdapter: QueryOrdersRecyclerAdapter, adminOrderFragment: AdminOrderFragment?) {
        val baseUrlString = "orders?q={\"cif_cliente\":\"$cif\"}"
        val urlStr = BASE_URL + baseUrlString

        compositeDisposable.add(
            retrofit.getOrdersByCif(urlStr).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(
                    { response ->
                        sharedPreferences?.setLoadingOrdersScreenValue(true)
                        adminOrderFragment!!.disableViewListeners()
                        onResponse(response, 11, R.string.string_vacio, orderCifAdapter,adminOrderFragment)

                    },
                    {onFailure(R.string.no_usuario_con_ese_cif) }
                ))
    }

    fun getOrdersByCifAndDate(cif:String,_date:String, orderCifAndDateAdapter: QueryOrdersRecyclerAdapter,adminOrderFragment: AdminOrderFragment?) {
        val baseUrlString = "orders?q={\"cif_cliente\":\"$cif\"}"
        val urlStr = BASE_URL + baseUrlString
        var date = _date
        if (date.length < 10) {
            var day = date.split("_")[0]
            var month = date.split("_")[1]
            val year = date.split("_")[2]

            if (day.length < 2)
                day = "0$day"
            if (month.length < 2)
                month = "0$month"
            date = "${day}_${month}_${year}"
        }

        compositeDisposable.add(
            retrofit.getOrdersByCifAndDate(urlStr).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(
                    { response ->
                        if ((response as List<Order>)[0].date.equals(date))
                        {
                            sharedPreferences?.setLoadingOrdersScreenValue(true)
                            adminOrderFragment!!.disableViewListeners()
                            onResponse(response,12, R.string.string_vacio,orderCifAndDateAdapter,adminOrderFragment)
                        }
                        else{
                            if (adminOrderFragment != null)
                            {
                                onFailure(R.string.no_usuario_con_ese_cif_con_pedido_en_esa_fecha) }
                                sharedPreferences?.setLoadingOrdersScreenValue(false)
                                adminOrderFragment?.enableViewListeners()
                            }
                        },
                    {
                        sharedPreferences?.setLoadingOrdersScreenValue(false)
                        adminOrderFragment?.enableViewListeners()
                        onFailure(R.string.no_usuario_con_ese_cif_con_pedido_en_esa_fecha)}
                ))
    }

    fun putOrderInDatabase(orderList:List<Order>) : Boolean {
        compositeDisposable.add(
            retrofit.sendNewOrderToDataBase(orderList).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response -> onResponse(response, 13, R.string.pedido_cargado_en_bd,null,null)},
                    { onFailure(R.string.no_puedo_registrar_pedido_en_bd) }
                ))
        return true
    }

    @SuppressLint("CheckResult")
    fun eraseOrderByDateAndUserCIF(_date:String, _cif:String, _adapter:QueryOrdersRecyclerAdapter, _adapterPosition:Int) {
        val date = _date
        val cif = _cif
        val adapter = _adapter
        val position = _adapterPosition
        val url = "${BASE_URL}orders?q={\"cif_cliente\":\"$cif\",\"date\":\"$date\"}"

        retrofit.getOrdersByCifAndDate(url).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
        { response ->
            onResponse(response,14,position,adapter,null)
        },
        { onFailure(R.string.problema_internet) })
    }

    private fun eraseOrderByID(_matchingOrderList:List<Order>,_adapter:QueryOrdersRecyclerAdapter, _adapterPosition:Int)
    {
        val matchingOrderList = _matchingOrderList
        val matchingIDList = mutableListOf<String>()
        matchingOrderList.forEach {
            it._id?.let { it1 -> matchingIDList.add(it1) }
        }

        val urlList = mutableListOf<String>()
        matchingIDList.forEach {
            urlList.add(BASE_URL + "orders/" + it)

            retrofit.deleteOrderById(BASE_URL + "orders/" + it)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                { onResponse(null, 15, R.string.pedido_borrado_con_exito, null, null) },
                {onFailure(R.string.no_puedo_eliminar_pedido) }
            )
        }
        _adapter.getList().removeAt(_adapterPosition)
        _adapter.notifyDataSetChanged()
    }

    fun getOrderByCifLiveData() = orderByIdLiveData

    fun getOrderByCifAndDateLiveData() = orderByCifAndDateLiveData

    fun showAToast(stringId:Int?)
    {
        if (stringId != null && stringId != 0)
            Handler(Looper.getMainLooper()).postDelayed( {
                Toast.makeText(context,context.resources.getString(stringId), Toast.LENGTH_SHORT).show()
            },200L)
    }

    internal fun onFailure(stringResource:Int) {
        showAToast(stringResource)
    }
    internal fun <T> onResponse (response:T?,successValue:Int,stringResource: Int?,adapter: T?,@Nullable _fragment:Fragment?) {
        when (successValue){
            0 ->  {
                userListLiveData.value = response as List<User>
            }
            1 -> {
                val user = User("","-1","","","","","","","","","", true)
                userLiveData.value = if (response == null ) user else response as User
            }
            6 -> {
                val productList: List<Product>
                if (adapter!=null) {
                    if ((response as List<Product>).isNotEmpty()) {
                        productList = response
                        GlobalScope.launch(Dispatchers.IO) {
                            database?.insertListInRoomDatabase(productList)
                            (adapter as AdminProductsRecyclerAdapter).setProductList(productList)
                            Handler(Looper.getMainLooper()).postDelayed({ adapter.notifyDataSetChanged() }, 200L)
                        }
                        productListLiveData.value = productList
                    } else
                        Toast.makeText(context, R.string.no_hay_productos, Toast.LENGTH_SHORT).show()
                }
                else
                    productListLiveData.value = response as List<Product>
            }
            7 -> {
                productLiveData.value = response as Product
            }
            8 -> {
                val fragment:AdminProductsFragment = _fragment as AdminProductsFragment
                val productList = mutableListOf<Product>()
                productList.addAll(response as List<Product>)

                if (productList.isNotEmpty()) {
                    GlobalScope.launch(Dispatchers.IO) {
                        database?.insertListInRoomDatabase(productList)

                        Handler(Looper.getMainLooper()).postDelayed( {
                            (adapter as AdminProductsRecyclerAdapter).setProductList(productList)
                            (adapter as AdminProductsRecyclerAdapter).notifyDataSetChanged()
                            Thread.sleep(1500)
                            sharedPreferences!!.setLoadingProductsScreenValue(false)
                            fragment.enableViewListeners()
                        },200L)
                    }
                }
            }
            11 -> {
                var orderList: List<Order> = response as List<Order>
                val fragment:AdminOrderFragment = _fragment as AdminOrderFragment
                sharedPreferences?.setLoadingOrdersScreenValue(true)
                fragment.disableViewListeners()

                val sortedList: MutableList<List<Order>> = mutableListOf()
                val changingIndexList = mutableListOf<Int>()
                var numberOfDays = 1

                if (orderList.isNotEmpty()) {
                    orderList = orderList.sortedBy { it.date }

                    for (i in 1 until orderList.size) {
                        if (!orderList[i - 1].date.equals(orderList[i].date)) {
                            changingIndexList.add(i)
                            numberOfDays++
                        }
                    }
                    var counter = 0
                    var limit:Int
                    for (i in 0 until numberOfDays) {
                        val list = mutableListOf<Order>()
                        if (i == numberOfDays - 1)
                            limit = orderList.size
                        else
                            limit = changingIndexList[i]
                        for (j in counter until limit) {
                            list.add(orderList[j])
                            counter = j+1
                        }
                        sortedList.add(list)
                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    (adapter as QueryOrdersRecyclerAdapter).setList(sortedList)
                    adapter.notifyDataSetChanged()
                    Thread.sleep(1500)
                    fragment.enableViewListeners()
                }, 200L)
            }

            12 -> {
                val orderList:List<Order> = response as List<Order>
                val fragment:AdminOrderFragment = _fragment as AdminOrderFragment
                Handler(Looper.getMainLooper()).postDelayed( {
                    (adapter as QueryOrdersRecyclerAdapter).setList(listOf(orderList))
                    adapter.notifyDataSetChanged()
                    orderByCifAndDateLiveData.value = orderList
                    Thread.sleep(1500)
                    sharedPreferences!!.setLoadingProductsScreenValue(false)
                    fragment.enableViewListeners()
                },200L)
            }

            14 -> {
                val matchingOrderList = (response as List<Order>)
                val position = stringResource
                eraseOrderByID(matchingOrderList, adapter as QueryOrdersRecyclerAdapter,position!!)
            }
            15 ->
                showAToast(stringResource)
        }

        if (stringResource != R.string.string_vacio )
            showAToast(stringResource)
    }
}
