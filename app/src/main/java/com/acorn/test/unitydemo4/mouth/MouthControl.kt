package com.acorn.test.unitydemo4.mouth

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.acorn.test.unitydemo4.bean.Subtitle
import com.acorn.test.unitydemo4.bean.TTSBean
import com.acorn.test.unitydemo4.tts.ITtsListener
import com.acorn.test.unitydemo4.utils.GsonUtils
import com.acorn.test.unitydemo4.utils.logI
import com.alibaba.idst.nui.INativeTtsCallback
import java.lang.ref.WeakReference

/**
 * 控制unity人物口型
 * Created by acorn on 2021/7/20.
 */
class MouthControl(private val listener: ITtsListener? = null) {
    private var lastJsonStr = ""
    private var startStamp: Long = 0
    private val handler = MouthControlHandler(this)
    private var curIndex = 0

    //最后一个发送出去的文本,注:这个和curIndex不是同一个体系,因为不发音的字符(如标点符号)也有Index.比如"hi,Alice",其中的A发音的index为3,但是curIndex为2
    private var lastSubtitleIndex: Int = -1
    private var curTtsTaskId: String = ""

    companion object {
        const val TAG = "TtsHelperMouthControl"
        const val MSG_SEND_SUBTITLE = 1000
        const val MSG_END_CALLBACK = 1001
        const val MSG_VOICE_END = 1002
    }

    private fun start() {
        startStamp = SystemClock.uptimeMillis()
    }

    fun onTtsEventCallback(event: INativeTtsCallback.TtsEvent, task_id: String) {
        if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_START) {
        } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_END) {
            curTtsTaskId = task_id
            callbackEnd()
        } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_PAUSE) {
            reset()
        } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_RESUME) {
        } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_ERROR) {
            reset()
        } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_CANCEL) {
            reset()
        }
    }

    private fun callbackEnd() {
        logI("MouthControl end()")
        //通知handler最后一个音节的begin_index
        handler.sendMessage(Message.obtain().apply {
            what = MSG_END_CALLBACK
            arg1 = lastSubtitleIndex
            obj = curTtsTaskId
        })
    }

    private fun reset() {
        logI("MouthControl reset()")
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
                    //第一个音节
                    msg.arg1 = 1
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
        private val weakMouthControl = WeakReference(mouthControl)
        private var lastWordsIndex = -1
        private var taskId = ""

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_SEND_SUBTITLE -> {
                    val bean = msg.obj as Subtitle
                    logI("handleMsg:${bean.text}")
                    if (msg.arg1 == 1) { //是第一个音节
                        weakMouthControl.get()?.listener?.onTtsVoiceStart()
                    } else if (bean.begin_index == lastWordsIndex) { //是最后一个发音
                        lastWordsIndex = -1
                        weakMouthControl.get()?.reset()
                        //等待音节结束
                        sendEmptyMessageDelayed(
                            MSG_VOICE_END,
                            (bean.end_time - bean.begin_time).toLong()
                        )
                    }
                }
                MSG_END_CALLBACK -> {
                    lastWordsIndex = msg.arg1
                    taskId = msg.obj as String
                }
                MSG_VOICE_END -> {
//                    taskId暂时没用
                    weakMouthControl.get()?.listener?.onTtsVoiceEnd()
                }
            }
        }
    }

}