package com.acorn.test.unitydemo4.unity

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import com.acorn.test.unitydemo4.bean.Subtitle
import com.acorn.test.unitydemo4.bean.TTSBean
import com.acorn.test.unitydemo4.utils.GsonUtils
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 控制unity人物口型
 * Created by acorn on 2021/7/20.
 */
class MouthControl {
    private var lastJsonStr = ""
    private var startStamp: Long = 0
    private val handler = MouthControlHandler(this)
    private var curIndex = 0

    //最后一个发送出去的文本,注:这个和curIndex不是同一个体系,因为不发音的字符(如标点符号)也有Index.比如"hi,Alice",其中的A发音的index为3,但是curIndex为2
    private var lastSubtitleIndex: Int = -1

    companion object {
        const val TAG = "TtsHelperMouthControl"
        const val MSG_SEND_SUBTITLE = 1000
        const val MSG_END_CALLBACK = 1001
    }

    private fun start() {
        startStamp = SystemClock.uptimeMillis()
    }

    fun callbackEnd() {
        Log.i(TAG, "MouthControl end()")
        //通知handler最后一个音节的begin_index
        handler.sendMessage(Message.obtain().apply {
            what = MSG_END_CALLBACK
            arg1 = lastSubtitleIndex
        })
    }

    fun reset() {
        Log.i(TAG, "MouthControl reset()")
        startStamp = 0L
        lastSubtitleIndex = -1
        curIndex = 0
        handler.removeCallbacksAndMessages(null)
    }


    fun onTtsCallback(speachData: String) {
//        Log.i(TAG,"MouthControl onTtsCallback() $isStarted")
//        if (!isStarted.get())
//            return
        if (speachData == lastJsonStr) //去掉重复的
            return
        lastJsonStr = speachData
        if (startStamp == 0L) {
            start()
        }
        val bean = GsonUtils.fromJsontoBean<TTSBean>(speachData, TTSBean::class.java)
        val length = bean.subtitles.size
        if (length > curIndex) {
            for (i in curIndex until length) {
                val subtitle = bean.subtitles[i]
                val msg = Message.obtain()
                msg.what = MSG_SEND_SUBTITLE
                msg.obj = subtitle
                if (i == 0) {
                    handler.sendMessage(msg)
                } else {
                    handler.sendMessageAtTime(msg, startStamp + subtitle.begin_time)
                }
                lastSubtitleIndex = subtitle.begin_index
            }
            curIndex = length
        }
    }


    class MouthControlHandler(mouthControl: MouthControl) : Handler(Looper.getMainLooper()) {
        val weakMouthControl = WeakReference(mouthControl)
        var lastWordsIndex = -1

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_SEND_SUBTITLE -> {
                    val bean = msg.obj as Subtitle
                    Log.i(TAG, "handleMsg:${bean.text}")
                    if (bean.begin_index == lastWordsIndex) { //是最后一个发音
                        lastWordsIndex = -1
                        weakMouthControl.get()?.reset()
                    }
                }
                MSG_END_CALLBACK -> {
                    lastWordsIndex = msg.arg1
                }
            }
        }
    }

}