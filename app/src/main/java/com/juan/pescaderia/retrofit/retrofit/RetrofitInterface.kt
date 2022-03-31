package com.juan.pescaderia.retrofit.retrofit

import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Call

import retrofit2.http.*



interface RetrofitInterface {

    companion object {
        private const val API_KEY =""
    }

    @Headers("x-apikey:$API_KEY")
    @GET("usuarios")
    fun getAllUsers() : Call<List<User>>

    @Headers("x-apikey:$API_KEY")
    @GET("usuarios")
    fun getUsers() : Observable<List<User>>

    @Headers("x-apikey:$API_KEY")
    @GET("usuarios/{id}")
    fun getAUserById(@Path ("id") id: String) : Observable<User>

    @Headers("x-apikey:$API_KEY")
    @POST("usuarios")
    fun createUser(@Body user:User) : Observable<User>

    @Headers("X-HTTP-Method-Override: PATCH", "cache-control: no-cache","x-apikey:$API_KEY")
    @POST("usuarios/{id}")
    fun updateUserById(@Path ("id") id: String, @Body user:User) : Observable<User>

    @Headers("x-apikey:$API_KEY")
    @DELETE("usuarios/{foreign_id}")
    fun deleteUserById(@Path ("foreign_id") foreign_id:String) : Completable

    @Headers("x-apikey:$API_KEY")
    @DELETE("usuarios/{id}")
    fun deleteAllUsers(@Path ("id") id:String) : Completable

    @Headers("x-apikey:$API_KEY")
    @GET("products")
    fun getAllProducts() : Observable<List<Product>>

    @Headers("X-HTTP-Method-Override: PATCH", "cache-control: no-cache","x-apikey:$API_KEY")
    @POST("products/{id}")
    fun updateProductById(@Path ("id") id: String, @Body product:Product) : Observable<Product>

    @Headers("x-apikey:$API_KEY")
    @POST("products")
    fun enterNewProductList(@Body product:List<Product>) : Observable<List<Product>>

    @Headers("x-apikey:$API_KEY")
    @DELETE("products/{foreign_id}")
    fun deleteProductById(@Path ("foreign_id") foreign_id:String) : Completable

    @Headers("x-apikey:$API_KEY")
    @DELETE("products/{id}")
    fun deleteAllProducts(@Path ("id") id:String) : Completable

    @Headers("apikey:$API_KEY")
    @GET()
    fun getOrdersByCif(@Url url:String) : Observable<List<Order>>

    @Headers("x-apikey:$API_KEY")
    @GET()
    fun getOrdersByCifAndDate(@Url url:String) : Observable<List<Order>>

    @Headers("x-apikey:$API_KEY")
    @GET()
    fun getOrdersByCifAndDateWithFullID(@Url url:String) : Observable<List<String>>

    @Headers("x-apikey:$API_KEY")
    @POST("orders")
    fun sendNewOrderToDataBase(@Body order:List<Order>) : Observable<List<Order>>

    @Headers("x-apikey:$API_KEY")
    @DELETE()
    fun deleteOrderById(@Url url:String) : Observable<Order>

    @Headers("content-type:application/json","cache-control:no-cache","x-apikey:$API_KEY")
    @POST("mail/{email}")
    fun sendRecoverEmail(@Path ("email") data:String) : Call<String>

}
