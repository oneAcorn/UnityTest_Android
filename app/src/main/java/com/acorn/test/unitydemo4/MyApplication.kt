package com.acorn.test.unitydemo4

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.tencent.mmkv.MMKV

/**
 * Created by acorn on 2021/7/23.
 */
class MyApplication : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(applicationContext)
        mContext = applicationContext
    }
}