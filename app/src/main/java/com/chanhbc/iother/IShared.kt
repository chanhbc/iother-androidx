package com.chanhbc.iother

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.util.*

@Suppress("unused")
class IShared @SuppressLint("CommitPrefEdits")
private constructor(mContext: Context) : SharedPreferences.OnSharedPreferenceChangeListener {
    private val mSharedPreferences: SharedPreferences = mContext.getSharedPreferences(mContext.packageName, Context.MODE_PRIVATE)
    private val mEditor = this.mSharedPreferences.edit()

    private val onISharedListeners = ArrayList<OnISharedListener>()

    init {
        registerChangeListener()
    }

    private fun registerChangeListener() {
        this.mSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun unregisterChangeListener() {
        this.mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    fun removeKey(key: String) {
        this.mEditor.remove(key).apply()
    }

    fun clear() {
        this.mEditor.clear().apply()
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return this.mSharedPreferences.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        this.mEditor.putBoolean(key, value).commit()
    }

    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return this.mSharedPreferences.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        this.mEditor.putInt(key, value).commit()
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = 0): Long {
        return this.mSharedPreferences.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        this.mEditor.putLong(key, value).commit()
    }

    @JvmOverloads
    fun getFloat(key: String, defaultValue: Float = 0.0f): Float {
        return this.mSharedPreferences.getFloat(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        this.mEditor.putFloat(key, value).commit()
    }

    @JvmOverloads
    fun getString(key: String, defaultValue: String = IConstant.EMPTY): String {
        return this.mSharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        this.mEditor.putString(key, value).commit()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        for (onISharedListener in onISharedListeners) {
            onISharedListener.onKeyChangeListener(sharedPreferences, key)
        }
    }

    fun addListener(onISharedListener: OnISharedListener) {
        if (onISharedListeners.indexOf(onISharedListener) < 0) {
            onISharedListeners.add(onISharedListener)
        }
    }

    fun removeListener(onISharedListener: OnISharedListener) {
        val index = onISharedListeners.indexOf(onISharedListener)
        if (index >= 0) {
            onISharedListeners.removeAt(index)
        }
    }

    interface OnISharedListener {
        fun onKeyChangeListener(sharedPreferences: SharedPreferences, key: String)
    }

    companion object {
        private lateinit var instance: IShared

        fun getInstance(context: Context): IShared {
            if (!::instance.isInitialized) {
                instance = IShared(context)
            }
            return instance
        }
    }
}
