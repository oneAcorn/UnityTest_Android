package com.acorn.test.unitydemo4.tts

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.acorn.test.unitydemo4.extends.md5
import com.acorn.test.unitydemo4.unity.MouthControl
import com.acorn.test.unitydemo4.utils.MyConstants
import com.acorn.test.unitydemo4.utils.Utils
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.alibaba.idst.nui.CommonUtils
import com.alibaba.idst.nui.Constants
import com.alibaba.idst.nui.INativeTtsCallback
import com.alibaba.idst.nui.INativeTtsCallback.TtsEvent
import com.alibaba.idst.nui.NativeNui
import java.util.*

/**
 * Created by acorn on 2021/7/20.
 */
class TtsHelper(private val context: Context, lifecycle: Lifecycle) {
    private val TAG = "TtsHelper"
    private val nui_tts_instance = NativeNui(Constants.ModeType.MODE_TTS)
    private var initialized = false
    private val mouthController = MouthControl()

    //  AudioPlayer默认采样率是16000
    private val mAudioTrack: AudioPlayer = AudioPlayer(object : AudioPlayerCallback {
        override fun playStart() {
            Log.i(TAG, "start play")
        }

        override fun playOver() {
            Log.i(TAG, "play over")
        }
    })

    init {
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.i(TAG, "onCreate")
                    if (!CommonUtils.copyAssetsData(context)) {
                        throw RuntimeException("copy assets failed")
                    }
                    if (Constants.NuiResultCode.SUCCESS == initialize()) {
                        initialized = true
                    } else {
                        Log.e(TAG, "init failed")
                        throw RuntimeException("init failed")
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    quitTts()
                }
                else -> {
                }
            }
        })

    }

    fun startTts(ttsText: String) {
        if (!initialized) {
            initialize()
        }
        nui_tts_instance.startTts("1", "", ttsText)
    }

    fun pauseTts() {
        nui_tts_instance.pauseTts()
        mAudioTrack.pause()
    }

    fun resumeTts() {
        nui_tts_instance.resumeTts()
        mAudioTrack.play()
    }

    fun cancelTts() {
        nui_tts_instance.cancelTts("")
        mAudioTrack.stop()
    }

    fun quitTts() {
        mAudioTrack.stop()
        nui_tts_instance.tts_release()
        initialized = false
    }

    private fun initialize(): Int {
        val ret = nui_tts_instance.tts_initialize(object : INativeTtsCallback {
            override fun onTtsEventCallback(event: TtsEvent, task_id: String, ret_code: Int) {
                Log.i(
                    TAG,
                    "tts event:$event task id $task_id ret $ret_code"
                )
                if (event == TtsEvent.TTS_EVENT_START) {
                    mAudioTrack.play()
                    Log.i(TAG, "start play")
                } else if (event == TtsEvent.TTS_EVENT_END) {
                    Log.i(TAG, "play end")
                    mouthController.callbackEnd()
                } else if (event == TtsEvent.TTS_EVENT_PAUSE) {
                    mAudioTrack.pause()
                    mouthController.reset()
                    Log.i(TAG, "play pause")
                } else if (event == TtsEvent.TTS_EVENT_RESUME) {
                    mAudioTrack.play()
                } else if (event == TtsEvent.TTS_EVENT_ERROR) {
                    mouthController.reset()
                } else if (event == TtsEvent.TTS_EVENT_CANCEL) {
                    mouthController.reset()
                }
            }

            /**
             * 合成数据回调
             * @param info：使用时间戳功能时，返回JSON格式的时间戳结果。
             * @param info_len: info字段的数据长度，暂不使用。
             * @param data：合成的音频数据，写入播放器。
             */
            override fun onTtsDataCallback(info: String, info_len: Int, data: ByteArray) {
                if (info.length > 0) {
                    Log.i(TAG, "info: $info")
                    mouthController.onTtsCallback(info)
                }
                if (data.size > 0) {
                    mAudioTrack.setAudioData(data)
                    Log.i(TAG, "write:" + data.size)
                }
            }

            override fun onTtsVolCallback(vol: Int) {
                Log.i(TAG, "tts vol $vol")
            }
        }, generatesTicket(), Constants.LogLevel.LOG_LEVEL_VERBOSE, true)

        if (Constants.NuiResultCode.SUCCESS != ret) {
            Log.i(TAG, "create failed")
        }
        nui_tts_instance.setparamTts("sample_rate", "16000")
        // 在线语音合成发音人可以参考阿里云官网
        nui_tts_instance.setparamTts("font_name", "siqi")
        nui_tts_instance.setparamTts("enable_subtitle", "1")
//        nui_tts_instance.setparamTts("speed_level", "1");
//        nui_tts_instance.setparamTts("pitch_level", "0");
//        nui_tts_instance.setparamTts("volume", "1.0");
        //        nui_tts_instance.setparamTts("speed_level", "1");
//        nui_tts_instance.setparamTts("pitch_level", "0");
//        nui_tts_instance.setparamTts("volume", "1.0");
        return ret
    }

    private fun generatesTicket(): String {
        var str = ""
        try {
            //获取token方式一般有两种：

            //方法1：
            //参考Auth类的实现在端上访问阿里云Token服务获取SDK进行获取
            //JSONObject object = Auth.getAliYunTicket();

            //方法2：（推荐做法）
            //在您的服务端进行token管理，此处获取服务端的token进行语音服务访问


            //请输入您申请的id与token，否则无法使用语音服务，获取方式请参考阿里云官网文档：
            //https://help.aliyun.com/document_detail/72153.html?spm=a2c4g.11186623.6.555.59bd69bb6tkTSc
            val jsonObject = JSONObject()
            jsonObject["app_key"] = MyConstants.NUI_APP_KEY
            jsonObject["token"] = MyConstants.NUI_TOKEN
            jsonObject["device_id"] = Utils.getDeviceId()
            jsonObject["url"] = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
            jsonObject["workspace"] = CommonUtils.getModelPath(context)
            // 设置为在线合成
            jsonObject["mode_type"] = "2"
            str = jsonObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
//        Log.i(TAG, "UserContext:$str")
        return str
    }
}