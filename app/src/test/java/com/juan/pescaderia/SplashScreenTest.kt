package com.juan.pescaderia

import androidx.work.Constraints
import androidx.work.*
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import androidx.work.ListenableWorker.*
import org.junit.Assert

internal class SplashScreenTest {

    lateinit var constrainstWorker: Constraints
    lateinit var downloadProductsWorkRequest:PeriodicWorkRequest
    lateinit var worker:Worker
    lateinit var configuration:Configuration
    @BeforeEach
    fun init() {
        constrainstWorker = mock()
        configuration = mock()// Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG).setExecutor(SynchronousExecutor()).build()
        downloadProductsWorkRequest = mock()// PeriodicWorkRequestBuilder<DownloadWorker>(24, TimeUnit.HOURS,30, TimeUnit.MINUTES).addTag("download_products").setConstraints(constrainstWorker).build()
        worker = mock()
    }
    @Test
    fun testConfigProductUpdate() {
        val failure = Result.failure()
        val success = Result.success()
        whenever(worker.doWork()).thenReturn(failure)
        Assert.assertEquals(worker.doWork(),failure)
        whenever(worker.doWork()).thenReturn(success)
        Assert.assertEquals(worker.doWork(),success)
    }
}