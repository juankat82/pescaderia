package com.juan.pescaderia.services

import android.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.dataclasses.TextMessage
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.*
import org.mockito.stubbing.Answer
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SmsServiceIntegrationTest {

    val smsService:SmsService = mock()
    val context = InstrumentationRegistry.getInstrumentation().context
    var smsIntent:Intent = mock()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val sms = mock<TextMessage>()
        val bundle = Bundle()
        bundle.putParcelable("sms",sms)
        smsIntent.putExtra("SmsBundle",bundle)
        context.startService(smsIntent)
    }

    @Test
    fun check_sms_permissions() {
        smsService.getPermissionsToSendSms()
        verify(smsService).getPermissionsToSendSms()
    }

    @Test
    fun check_sms_is_sent() {
        smsService.sendSms()
        verify(smsService).sendSms()
    }

    @Test
    fun test_if_toast_shows_using_resource_string()
    {
        var returnValue:String = ""
        whenever(smsService.showAToast(R.string.ok)).thenAnswer(Answer { returnValue = R.string.ok.toString() })
        smsService.showAToast(R.string.ok)
        Assert.assertEquals(returnValue,R.string.ok.toString())
    }

    @After
    fun stopSmsService() {
        context.stopService(smsIntent)
    }
}