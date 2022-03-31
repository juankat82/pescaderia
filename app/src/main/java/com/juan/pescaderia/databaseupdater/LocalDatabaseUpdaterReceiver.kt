package com.juan.pescaderia.databaseupdater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.juan.pescaderia.SplashScreen

class LocalDatabaseUpdaterReceiver : BroadcastReceiver() {

    override fun onReceive(_context: Context, intent: Intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
        {
            val activitySplash = _context.applicationContext as SplashScreen
            activitySplash.configProductUpdate()
        }
    }
}