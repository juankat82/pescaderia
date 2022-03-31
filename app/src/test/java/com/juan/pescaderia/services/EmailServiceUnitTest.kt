package com.juan.pescaderia.services

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat.startActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.databaseupdater.RemoteDatabaseUploaderReceiver
import com.juan.pescaderia.dataclasses.Email
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.stubbing.Answer
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class EmailServiceUnitTest {

    val context = InstrumentationRegistry.getInstrumentation().context
    val emailService:EmailService = mock()
    val emailIntent: Intent = mock()
    val user: User = mock()
    val productList:List<Product> = mock()
    var receiver:RemoteDatabaseUploaderReceiver = mock()
    val filter:IntentFilter = mock()
    val queryService:QueryService = mock()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun init() {
        context.registerReceiver(receiver,filter)
    }

    @Test
    fun test_add_order_to_remote_database_using_intent_receiver() {
        val spyUploader = spy(emailService)
        val ret = 100

        doReturn(ret).whenever(emailService).addToRemoteOrderDatabase(any(),any())
        emailService.addToRemoteOrderDatabase(any(),any())
    }
}