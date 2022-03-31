package com.juan.pescaderia.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.juan.pescaderia.R
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import java.util.*

private const val PRIMARY_CHANNEL_ID ="primary_notification_channel"

class DownloadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    val context = appContext
    private lateinit var mNotifyManager: NotificationManager
    private lateinit var mNotificationChannel: NotificationChannel
    private lateinit var sharedPreferencesController: SharedPreferencesController
    private var isNotificationsAllowed = true
    private var minimumDelayTime = 86400000L

    override fun doWork(): Result {

        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(context)!!
        val latestUpdateTime = sharedPreferencesController.getLatestUpdateTime()


        isNotificationsAllowed = sharedPreferencesController.isNotificationsAllowed()

        if (latestUpdateTime >= (minimumDelayTime/3)*2 || latestUpdateTime == 0L)
        {
            setNotificationChannel()
            val queryService = QueryService.getInstance(applicationContext)
            if (queryService != null) {
                queryService.getAllProducts(true, null)
                if (isNotificationsAllowed)
                    emitNotification()
                sharedPreferencesController.setLastDatabaseUpdateDate(Calendar.getInstance().timeInMillis)
                // Indicate whether the work finished successfully with the Result
                return Result.success()
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.failure()
    }
    fun setMinimumTime(time:Long) {
        minimumDelayTime = time
    }
    private fun setNotificationChannel()
    {
        mNotificationChannel = NotificationChannel(
            PRIMARY_CHANNEL_ID,"Actualizacion de lista de productos",
            NotificationManager.IMPORTANCE_HIGH)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationChannel.enableLights(true)
            mNotificationChannel.enableVibration(true)
            mNotificationChannel.lightColor = Color.RED
            mNotificationChannel.description = "Actualizacion de lista de productos"
            mNotifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyManager.createNotificationChannel(mNotificationChannel)
        }
    }
    private fun emitNotification()
    {
        val pendingIntent = PendingIntent.getActivity(context,0, Intent(),0)

        val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID).setContentTitle(applicationContext.resources.getString(
            R.string.app_name))
            .setContentText("Productos Actualizados!")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_job_notif)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)){
            notify(0, builder)
        }
    }
}