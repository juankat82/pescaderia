package com.juan.pescaderia.databaseupdater

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.TextMessage
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.services.SmsService
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.stubbing.Answer

private const val PHONE_NUMBER = "+0000000000000"

internal class MockRemoteDatabaseUploaderReceiverTest {

    private lateinit var queryInstance:QueryService
    private lateinit var conManager:ConnectivityManager
    private lateinit var netInfo: NetworkInfo
    private var isNetUp = true
    private lateinit var context:Context
    private lateinit var service: Service
    private lateinit var queryService:QueryService
    private lateinit var testDispatcher:TestCoroutineDispatcher
    private lateinit var testScope:TestCoroutineScope
    private lateinit var user: User
    private lateinit var order: Order

    @BeforeEach
    fun init() {
        queryInstance = mock()
        conManager = mock()
        netInfo = mock()
        isNetUp = checkNetActive()
        context = mock()
        service = mock()
        queryService = mock()
        testDispatcher = TestCoroutineDispatcher()
        testScope = TestCoroutineScope(testDispatcher)
        user = mock()
        order = mock()
    }

    @Test
    fun onReceive() {
        val test = testScope.runBlockingTest {
            val isAllowed = queryService.checkForSmsPermission(user, { sendSms() })
            Assert.assertFalse(isAllowed)
            whenever(queryService.checkForSmsPermission(any(),any())).thenAnswer({ true})
            val succesful = queryService.checkForSmsPermission(user,::sendSms)
            Assert.assertEquals(succesful,true)
        }
    }

    @Test
    fun uploadOrder() = testScope.runBlockingTest {
        whenever(queryService.sendOrder(any(),any())).thenAnswer({true})
        val answer = queryService.sendOrder(listOf(order),user)
        queryService.showAToast(0)
        Assert.assertEquals(answer,true)
        inOrder(queryService){
            verify(queryService).sendOrder(any(),any())
            verify(queryService).showAToast(any())
        }
    }

    fun checkNetActive() : Boolean{
        if (netInfo!=null)
            return true
        return false
    }

    fun sendSms() {
        val sms = TextMessage(user.telephone, PHONE_NUMBER,context.resources.getString(R.string.tienes_un_pedido).plus("${user.name} ${user.surname}"))
        val bundle = Bundle()
        bundle.putParcelable("sms",sms)
        val smsIntent = Intent(context, SmsService::class.java)
        smsIntent.putExtra("SmsBundle",bundle)
        context.startService(smsIntent)
    }
}