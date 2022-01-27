package com.techyourchance.multithreading.common

import android.os.Handler
import android.os.Looper

open class UiThreadPoster {
    private val mUiHandler: Handler

    /**
     * Execute [Runnable] on application's UI thread.
     * @param runnable [Runnable] instance containing the code that should be executed
     */
    fun post(runnable: Runnable?) {
        mUiHandler.post(runnable!!)
    }

    /**
     * The only reason this method exists is that UiThreadPosterTestDouble can override
     * it.
     */
    val mainHandler: Handler
        get() = Handler(Looper.getMainLooper())

    init {
        mUiHandler = mainHandler
    }
}