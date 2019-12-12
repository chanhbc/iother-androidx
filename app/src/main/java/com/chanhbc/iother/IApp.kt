package com.chanhbc.iother

import android.app.Application
import android.content.Context

@Deprecated("Application is no longer used in AndroidX")
open class IApp : Application() {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: IApp

        val context: Context
            @Synchronized get() = instance
    }
}