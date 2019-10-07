package com.chanhbc.todo

import android.app.Application
import android.content.Context

class IApp : Application() {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: IApp

        val context: Context
            @Synchronized get() = instance
    }
}
