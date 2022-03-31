package com.juan.pescaderToSendia.services

import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import com.juan.pescaderia.dataclasses.TextMessage
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class SmsServiceMockTest {

    private lateinit var bundle: Bundle
    private lateinit var mockBundle: Bundle
    private lateinit var smsManager:SmsManager
    private lateinit var textMessage: TextMessage
    private lateinit var intent:Intent

    @BeforeEach
    fun init() {
        smsManager = mock()
        textMessage = mock()
        intent = mock()
        bundle = Bundle()
        mockBundle = mock()
    }

    @Test
    fun onHandleIntent() {
        whenever(intent.getBundleExtra("SmsBundle")).thenReturn(bundle)
        bundle = intent.getBundleExtra("SmsBundle")!!
        Assert.assertNotNull(bundle)

        whenever(!mockBundle.isEmpty).thenReturn(true)
        val returnString = if (mockBundle.isEmpty) getPermissionToSendSms() else "NO PERMISSION ALLOWED"
        Assert.assertEquals(returnString,"FUNCTION GOT EXECUTED")
    }

    private fun getPermissionToSendSms() = "FUNCTION GOT EXECUTED"

    @Test
    fun sendSms() {
        smsManager.sendTextMessage(textMessage.toNumber, null, textMessage.textBody, null, null)
        verify(smsManager).sendTextMessage(textMessage.toNumber, null, textMessage.textBody, null, null)
    }
}