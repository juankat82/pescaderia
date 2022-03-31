package com.juan.pescaderia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.services.DownloadWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class SplashScreen : AppCompatActivity(){

    private lateinit var sharedPreferencesController:SharedPreferencesController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configProductUpdate()
        goFullScreen()
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }, 2000)
    }

    fun configProductUpdate() : Boolean
    {
        val constraintsWorker = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(false).setRequiresCharging(false).setRequiresDeviceIdle(false).setTriggerContentMaxDelay(
            Duration.ZERO).setRequiresStorageNotLow(false).build()
        val downloadProductsWorkRequest = PeriodicWorkRequestBuilder<DownloadWorker>(24,TimeUnit.HOURS,30,TimeUnit.MINUTES).addTag("download_products").setConstraints(constraintsWorker).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("downloadProducts", ExistingPeriodicWorkPolicy.KEEP,downloadProductsWorkRequest)
        return true
    }
    private fun goFullScreen()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        val constraintsWorker = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(false).setRequiresCharging(false).setRequiresDeviceIdle(false).setTriggerContentMaxDelay(
            Duration.ZERO).setRequiresStorageNotLow(false).build()
        val downloadProductsWorkRequest = PeriodicWorkRequestBuilder<DownloadWorker>(24,TimeUnit.HOURS,30,TimeUnit.MINUTES).addTag("download_products").setConstraints(constraintsWorker).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("downloadProducts", ExistingPeriodicWorkPolicy.KEEP,downloadProductsWorkRequest)
        super.onDestroy()
    }
}
