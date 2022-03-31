package com.juan.pescaderia.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Toast
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.TextMessage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.lang.Exception

class SmsService : IntentService("SmsService"){

    private var bundle: Bundle? = Bundle()
    private lateinit var textMessage:TextMessage
    private val smsManager = SmsManager.getDefault()

    override fun onHandleIntent(intent: Intent?) {
        bundle = intent?.getBundleExtra("SmsBundle")

        if (bundle!!.isEmpty)
            showAToast(R.string.sms_no_enviado)
        else {
            textMessage = bundle?.get("sms") as TextMessage
            getPermissionsToSendSms()
        }
    }


    fun getPermissionsToSendSms()
    {
        try {
            Dexter.withContext(this)
                .withPermission(android.Manifest.permission.SEND_SMS)
                .withListener( object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        sendSms()
                    }
                    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                        showAToast(R.string.permission_ungranted)
                        p1!!.continuePermissionRequest()
                    }
                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        showAToast(R.string.permission_not_granted)
                    }
                }).check()
            showAToast(R.string.sms_enviado)
        }
        catch (e:Exception) {
            showAToast(R.string.sms_no_enviado)
        }
    }
    fun sendSms() {
       smsManager.sendTextMessage(textMessage.toNumber, null, textMessage.textBody, null, null)
    }

    fun showAToast(stringId:Int)
    {
        Handler(Looper.getMainLooper()).postDelayed( {
            Toast.makeText(this,resources.getString(stringId), Toast.LENGTH_SHORT).show()
        },200L)
    }
}