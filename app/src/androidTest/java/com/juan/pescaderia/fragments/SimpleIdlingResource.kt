package com.juan.pescaderia.fragments


import androidx.annotation.Nullable
import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class SimpleIdlingResource : IdlingResource {

    @Nullable
    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    var activeResources = AtomicInteger(0)

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return activeResources.toInt() < 1
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    fun incrementBy(incrementValue:Int) {
        if (activeResources.addAndGet(incrementValue) <1 && callback !=null)
            callback!!.onTransitionToIdle()
    }
}