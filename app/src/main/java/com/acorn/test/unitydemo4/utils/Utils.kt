package com.acorn.test.unitydemo4.utils

import android.os.Build
import android.util.Log
import com.acorn.test.unitydemo4.extends.md5
import java.io.File
import java.util.*

/**
 * Created by acorn on 2021/7/21.
 */
object Utils {
    private val TAG = "Utils"

    fun getDeviceId(): String {
        return Build.SERIAL ?: getRandomUniqueIdentification()
    }

    /**
     * 唯一标识（目前只是呼叫用）
     */
    fun getRandomUniqueIdentification(): String {
        val random1 = UUID.randomUUID().toString()
        val random2 = UUID.randomUUID().toString()
        val random3 = UUID.randomUUID().toString()
        return (random1 + random2 + random3).md5()
    }

    fun createDir(dirPath: String): Int {
        var dirPath = dirPath
        val dir = File(dirPath)
        //文件夹是否已经存在
        if (dir.exists()) {
            Log.w(
                TAG,
                "The directory [ $dirPath ] has already exists"
            )
            return 1
        }
        if (!dirPath.endsWith(File.separator)) { //不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator
        }

        //创建文件夹
        if (dir.mkdirs()) {
            Log.d(TAG, "create directory [ $dirPath ] success")
            return 0
        }
        Log.e(TAG, "create directory [ $dirPath ] failed")
        return -1
    }
}