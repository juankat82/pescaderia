package com.juan.pescaderia.retrofit.retrofit

import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.User
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

internal class QueryServiceMockTest {

    private lateinit var retrofit:RetrofitInterface
    private lateinit var queryServiceMock:QueryService
    private var result:String = ""
    lateinit var callback:Callback<List<User>>

    @BeforeEach
    fun init() {
        retrofit = mock()
        queryServiceMock = mock()
        callback = mock()
    }

    @Test
    fun getAllUsersLiveDataTest() {
        doAnswer {
            getAllUsersLiveDataTest()
            val call:Call<List<User>> = mock()
            callback.onFailure(call,Throwable("error"))
            verify(callback).onFailure(any(),any())
        }

    }
    fun getAllUsersLiveData(callback:Callback<List<User>>) {
         object: Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                val call:Call<List<User>> = mock()
                callback.onFailure(call,Throwable("error"))
                println("failed")
                Assert.fail("failed")
            }
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                callback.onResponse(call,response)
            }
        }
    }


    fun findUserToRecover() {
    }

    fun sendOrder() {
    }

    fun locateAndSetUserInSystem() {
    }

    fun checkForSmsPermission() {
    }

    fun checkEmailForPermission() {
    }

    fun getAllUsersForSpinner() {
    }

    fun createUserInRemoteDatabase() {
    }

    fun updateUserData() {
    }

    fun deleteUserById() {
    }

    fun deleteAllUsers() {
    }

    fun getAllProducts() {
    }

    fun putProductListInDatabase() {
    }

    fun deleteAProductById() {
    }

    fun getOrdersByCustomerCif() {
    }

    fun getOrdersByCifAndDate() {
    }

    fun putOrderInDatabase() {
    }

    fun eraseOrderByDateAndUserCIF() {
    }

    fun getOrderByCifLiveData() {
    }

    fun getOrderByCifAndDateLiveData() {
    }


    fun onFailure() = false


    fun onResponse() = true
}