package com.juan.pescaderia.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.util.*

internal class DownloadWorkerMockTest {

    private lateinit var mNotifyManager: NotificationManager
    private lateinit var mNotificationChannel: NotificationChannel
    private lateinit var sharedPreferencesController: SharedPreferencesController
    private var minimumDelayTime = 86400000L

    @BeforeEach
    fun init() {
        mNotifyManager = mock()
        mNotificationChannel = mock()
        sharedPreferencesController = mock()
    }

    @Test
    fun doWork() {
        val onFailure = Result.failure<java.lang.IllegalArgumentException>(IllegalArgumentException())
        val onSuccess = Result.success("Well done!")
        val queryService:QueryService = mock()
        val product: Product = mock()
        val timeMillis = Calendar.getInstance().timeInMillis

        whenever(sharedPreferencesController.getLatestUpdateTime()).thenReturn(70000000L)
        val latestUpdateTime = sharedPreferencesController.getLatestUpdateTime()
        Assert.assertEquals(latestUpdateTime,70000000L)
        Assert.assertTrue(latestUpdateTime >= (minimumDelayTime/3)*2)

        whenever(queryService.getAllProducts(true,null)).thenReturn(MutableLiveData(listOf(product)))
        whenever(sharedPreferencesController.setLastDatabaseUpdateDate(any())).thenReturn(true)
        val prodLiveData = queryService.getAllProducts(true,null).value
        Thread.sleep(1000L)
        Assert.assertTrue(prodLiveData?.size != 0)
        val result = if (sharedPreferencesController.setLastDatabaseUpdateDate(timeMillis))
            onSuccess
        else
            onFailure

        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun setNotificationChannel() {
        val version = 28

        if (version >= 26)
            mNotifyManager.createNotificationChannel(mNotificationChannel)

        verify (mNotifyManager).createNotificationChannel(mNotificationChannel)
    }

    @Test
    fun emitNotification() {
        val notification: Notification = mock()
        val notificationManagerCompat:NotificationManagerCompat = mock()
        notificationManagerCompat.notify(0,notification)
        verify (notificationManagerCompat).notify(0,notification)
        
    }
}