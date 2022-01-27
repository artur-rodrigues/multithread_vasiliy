package com.techyourchance.multithreading.common

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class BackgroundThreadPoster {
    /**
     * Get the underlying [ThreadPoolExecutor].
     * In general, this method shouldn't be used and is provided only for the purpose of
     * integration with existing libraries and frameworks.
     */
    protected val threadPoolExecutor: ThreadPoolExecutor

    /**
     * Execute [Runnable] on a random background thread.
     * @param runnable [Runnable] instance containing the code that should be executed
     */
    fun post(runnable: Runnable?) {
        threadPoolExecutor.execute(runnable)
    }

    /**
     * Get the underlying [ThreadFactory].
     * In general, this method shouldn't be used and is provided only for the purpose of
     * integration with existing libraries and frameworks.
     */
    protected val threadFactory: ThreadFactory
        protected get() = threadPoolExecutor.threadFactory

    /**
     * This factory method constructs the instance of [ThreadPoolExecutor] that is used by
     * [BackgroundThreadPoster] internally.<br></br>
     * The returned executor has sensible defaults for Android applications.<br></br>
     * Override only if you're ABSOLUTELY sure that you know what you're doing.
     */
    protected fun newThreadPoolExecutor(): ThreadPoolExecutor {
        return ThreadPoolExecutor(
            CORE_THREADS, Int.MAX_VALUE,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            SynchronousQueue()
        )
    }

    companion object {
        private const val CORE_THREADS = 3
        private const val KEEP_ALIVE_SECONDS = 60L
    }

    init {
        threadPoolExecutor = newThreadPoolExecutor()
    }
}