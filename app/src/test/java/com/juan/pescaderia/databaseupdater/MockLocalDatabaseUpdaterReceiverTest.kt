package com.juan.pescaderia.databaseupdater

import android.content.Intent
import android.content.IntentFilter
import com.juan.pescaderia.SplashScreen
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class MockLocalDatabaseUpdaterReceiverTest {

    private lateinit var splashActivityMock:SplashScreen
    private lateinit var intent:Intent
    private val filter = "android.intent.action.BOOT_COMPLETED"
    private val execStr =  true

    @BeforeEach
    fun init () {
        splashActivityMock = mock()
        intent = mock()
        intent.action = filter
    }

    @Test
    fun onReceive() {
        whenever(intent.action).thenReturn(filter)
        Assert.assertTrue(intent.action.equals(filter))

        whenever(splashActivityMock.configProductUpdate()).thenReturn(true)
        Assert.assertTrue(splashActivityMock.configProductUpdate())

        val spiedMockActivity = spy(splashActivityMock)
        doReturn(execStr).whenever(spiedMockActivity).configProductUpdate()
        Assert.assertTrue(splashActivityMock.configProductUpdate())
    }
}