package com.acorn.test.unitydemo4.kws

import android.Manifest
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.acorn.test.unitydemo4.bean.KWSBean
import com.acorn.test.unitydemo4.extends.requestPermission
import com.acorn.test.unitydemo4.kws.inputstream.InFileStream
import com.acorn.test.unitydemo4.utils.GsonUtils
import com.acorn.test.unitydemo4.utils.logI
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import org.json.JSONObject
import java.util.*

/**
 * 语音唤醒
 * Created by acorn on 2021/7/23.
 */
class BaiduKwsHelper(
    private val activity: FragmentActivity,
    private val listener: IBaiduKwsListener? = null
) : EventListener {
    private var wakeup: EventManager? = null

    init {
        activity.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START->{
                    start()
                }
                Lifecycle.Event.ON_STOP -> {
                    stop()
                }
                else -> {
                }
            }
        })
    }

    fun init() {
        activity.requestPermission(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            allPermGrantedCallback = {
                // 基于SDK唤醒词集成1.1 初始化EventManager
                wakeup = EventManagerFactory.create(activity, "wp")
                // 基于SDK唤醒词集成1.3 注册输出事件
                wakeup?.registerListener(this) //  EventListener 中 onEvent方法
                logI("init finish")
            },
            anyPermDeniedCallback = {
                listener?.onKwsError("permission denied")
            })
    }

    fun start() {
        wakeup ?: return
        // 基于SDK唤醒词集成第2.1 设置唤醒的输入参数
        val params: MutableMap<String?, Any?> = TreeMap()
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        params[SpeechConstant.WP_WORDS_FILE] = "assets:///WakeUp_xiaofeng.bin"
        params["appid"] = "24594627"
        params["APP_KEY"] = "5jaX4tDI8D2BLfffFp6qNNXE"
        params["SECRET"] = "zvnslMT9Aue5LjUMz0jpVv4axe28S1KQ"
        // "assets:///WakeUp_xiaofeng.bin" 表示WakeUp.bin文件定义在assets目录下
        InFileStream.setContext(activity)
        var json: String? = null // 这里可以替换成你需要测试的json
        json = JSONObject(params).toString()
        wakeup?.send(SpeechConstant.WAKEUP_START, json, null, 0, 0)
        logI("wakeup start")
    }

    fun stop() {
        wakeup?.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0)
        logI("wakeup stop")
    }

    //  基于SDK唤醒词集成1.2 自定义输出事件类 EventListener  回调方法
    // 基于SDK唤醒3.1 开始回调事件
    override fun onEvent(
        name: String,
        params: String?,
        data: ByteArray?,
        offset: Int,
        length: Int
    ) {
        var logTxt = "name: $name"
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :$params"
        } else if (data != null) {
            logTxt += " ;data length=" + data.size
        }
        logI("kws onEvent:$logTxt")

        if (null != params) {
            val bean = GsonUtils.fromJsontoBean<KWSBean>(params, KWSBean::class.java)
            if (bean.errorCode == 0) { //success
                if (bean.word?.isEmpty() == false) {
                    listener?.onKwsResult(bean.word)
                } else {
                    listener?.onKwsError("awake word is null")
                }
            } else {
                listener?.onKwsError("awake failed", bean.errorCode)
            }
        }
    }
}