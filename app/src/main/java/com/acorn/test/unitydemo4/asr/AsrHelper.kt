package com.acorn.test.unitydemo4.asr

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.acorn.test.unitydemo4.bean.ASRBean
import com.acorn.test.unitydemo4.extends.requestPermission
import com.acorn.test.unitydemo4.utils.GsonUtils
import com.acorn.test.unitydemo4.utils.MyConstants
import com.acorn.test.unitydemo4.utils.Utils
import com.acorn.test.unitydemo4.utils.logI
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.alibaba.idst.nui.*
import com.alibaba.idst.nui.Constants.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by acorn on 2021/7/20.
 */
class AsrHelper(private val context: FragmentActivity, private val listener: IAsrListener? = null) :
    INativeNuiCallback {
    private var nuiInstance = NativeNui()
    private val WAVE_FRAM_SIZE = 20 * 2 * 1 * 16000 / 1000 //20ms audio for 16k/16bit/mono

    private val SAMPLE_RATE = 16000
    private var mAudioRecorder: AudioRecord? = null
    var mInit = false
    private lateinit var mHanderThread: HandlerThread

    //线程中运行
    private lateinit var mHandler: Handler
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    //是否正在听
    private var isListening = AtomicBoolean(false)

    companion object {
        const val TAG = "AsrHelper"
    }

    init {
        context.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    nuiInstance.release()
                }
                else -> {
                }
            }
        })
    }

    fun init(){
        context.requestPermission(Manifest.permission.RECORD_AUDIO,
            allPermGrantedCallback = {
                mHanderThread = HandlerThread("process_thread")
                mHanderThread.start()
                mHandler = Handler(mHanderThread.looper)
                initialize()
            },
            anyPermDeniedCallback = {
                mInit = false
                listener?.onAsrError("permission denied")
            })
    }

    /**
     * 开始监听对话,不是打开弹框!
     */
    fun startDialog() {
        logI("startDialog init:$mInit,isListening:${isListening.get()}")
        if (!mInit)
            return
        if (isListening.get()) //已经开始监听了
            return
        updateListeningState(true)
        mHandler.post {
            val ret: Int = nuiInstance.startDialog(VadMode.TYPE_P2T, genDialogParams())
            Log.i(TAG, "start done with $ret")
        }
    }

    /**
     * 关闭对话监听,不是关闭弹框!
     */
    fun stopDialog() {
        if (!mInit)
            return
        if (!isListening.get())
            return
        updateListeningState(false)
        mHandler.post {
            val ret: Long = nuiInstance.stopDialog().toLong()
            Log.i(TAG, "cancel dialog $ret end")
        }
    }

    private fun updateListeningState(isListening: Boolean) {
        mainThreadHandler.post {
            this.isListening.set(isListening)
            listener?.onAsrStateChanged(isListening)
        }
    }

    private fun initialize() {
        if (mInit)
            return
        Log.i(TAG, "initialize")
        //获取工作路径
        val assetPath = CommonUtils.getModelPath(context)
        Log.i(TAG, "use workspace $assetPath")

        var debugpath: String? = null
        context.externalCacheDir?.let {
            debugpath = it.absolutePath + "/debug_" + System.currentTimeMillis()
            Utils.createDir(debugpath!!)
        }

        //录音初始化，录音参数中格式只支持16bit/单通道，采样率支持8K/16K
        mAudioRecorder = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            WAVE_FRAM_SIZE * 4
        )

        //这里主动调用完成SDK配置文件的拷贝

        //这里主动调用完成SDK配置文件的拷贝
        if (CommonUtils.copyAssetsData(context)) {
            Log.i(TAG, "copy assets data done")
        } else {
            Log.i(TAG, "copy assets failed")
            return
        }

        //初始化SDK，注意用户需要在Auth.getAliYunTicket中填入相关ID信息才可以使用。

        //初始化SDK，注意用户需要在Auth.getAliYunTicket中填入相关ID信息才可以使用。
        val ret = nuiInstance.initialize(
            this,
            generatesInitParams(assetPath, debugpath),
            Constants.LogLevel.LOG_LEVEL_VERBOSE,
            true
        )
        Log.i(TAG, "result = $ret")
        if (ret == Constants.NuiResultCode.SUCCESS) {
            mInit = true
        }

        //设置相关识别参数，具体参考API文档
        nuiInstance.setParams(generatesParams())
    }

    private fun genDialogParams(): String {
        var params = ""
        try {
            val dialogParam = JSONObject()
            params = dialogParam.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "dialog params: $params")
        return params
    }

    private fun generatesParams(): String {
        var params = ""
        try {
            val nlsConfig = JSONObject()
            nlsConfig["enable_intermediate_result"] = true
            //            参数可根据实际业务进行配置
//            nlsConfig.put("enable_punctuation_prediction", true);
//            nlsConfig.put("enable_inverse_text_normalization", true);
            /**
             * 是否启动语音检测。默认值：False
             */
            nlsConfig.put("enable_voice_detection", true);
//            nlsConfig.put("customization_id", "test_id");
//            nlsConfig.put("vocabulary_id", "test_id");
            /**
            当enabble_voice_detection设置为true时，该参数生效。表示允许的最大开始静音时长。
            单位：毫秒。超出后（即开始识别后多长时间没有检测到声音）服务端将会发送TaskFailed事件，结束本次识别。
             */
            nlsConfig.put("max_start_silence", 5000);
            /**
            当enable_voice_detection设置为true时，该参数生效。表示允许的最大结束静音时长。
            单位：毫秒，取值范围：200ms～2000ms。超出时长服务端会发送RecognitionCompleted事件，结束本次识别
            （需要注意的是后续的语音不会继续进行识别）。
             */
            nlsConfig.put("max_end_silence", 800);
//            nlsConfig.put("sample_rate", 16000);
//            nlsConfig.put("sr_format", "opus");
            val parameters = JSONObject()
            parameters["nls_config"] = nlsConfig
            parameters["service_type"] = Constants.kServiceTypeASR
            //            如果有HttpDns则可进行设置
//            parameters.put("direct_ip", Utils.getDirectIp());
            params = parameters.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return params
    }

    private fun generatesInitParams(workpath: String, debugpath: String?): String {
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

            //token 24小时过期，因此需要通过阿里云SDK来进行更新
            jsonObject["app_key"] = MyConstants.NUI_APP_KEY
            jsonObject["token"] = MyConstants.NUI_TOKEN
            jsonObject["url"] = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
            jsonObject["device_id"] = Utils.getDeviceId()
            jsonObject["workspace"] = workpath
            if (null != debugpath)
                jsonObject["debug_path"] = debugpath
            jsonObject["sample_rate"] = "16000"
            jsonObject["format"] = "opus"
            //            object.put("save_wav", "true");
            str = jsonObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "InsideUserContext:$str")
        return str
    }

    //当回调事件发生时调用
    override fun onNuiEventCallback(
        event: NuiEvent?, resultCode: Int, arg2: Int, kwsResult: KwsResult?,
        asrResult: AsrResult?
    ) {
        Log.i(TAG, "event=$event")
        if (event == NuiEvent.EVENT_ASR_RESULT) { //结束监听
            //success result:asrResult?.asrResult
            updateListeningState(false)
            val bean = GsonUtils.fromJsontoBean<ASRBean>(asrResult?.asrResult, ASRBean::class.java)
            listener?.onAsrResult(bean?.payload?.result)
        } else if (event == NuiEvent.EVENT_ASR_PARTIAL_RESULT) {
            //识别中...asrResult?.asrResult
        } else if (event == NuiEvent.EVENT_ASR_ERROR) {
            Toast.makeText(context, "err:$resultCode", Toast.LENGTH_SHORT).show()
            updateListeningState(false)
        }
    }

    //当调用NativeNui的start后，会一定时间反复回调该接口，底层会提供buffer并告知这次需要数据的长度
    //返回值告知底层读了多少数据，应该尽量保证return的长度等于需要的长度，如果返回<=0，则表示出错
    override fun onNuiNeedAudioData(buffer: ByteArray?, len: Int): Int {
        var ret = 0
        if (mAudioRecorder!!.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "audio recorder not init")
            return -1
        }
        ret = mAudioRecorder!!.read(buffer!!, 0, len)
        return ret
    }

    //当录音状态发送变化的时候调用
    override fun onNuiAudioStateChanged(state: AudioState?) {
        Log.i(TAG, "onNuiAudioStateChanged")
        if (state == AudioState.STATE_OPEN) {
            Log.i(TAG, "audio recorder start")
            mAudioRecorder?.startRecording()
            Log.i(TAG, "audio recorder start done")
        } else if (state == AudioState.STATE_CLOSE) {
            Log.i(TAG, "audio recorder close")
            mAudioRecorder?.release()
        } else if (state == AudioState.STATE_PAUSE) {
            Log.i(TAG, "audio recorder pause")
            mAudioRecorder?.stop()
        }
    }

    override fun onNuiAudioRMSChanged(value: Float) {
        Log.i(TAG, "onNuiAudioRMSChanged vol $value")
    }

    override fun onNuiVprEventCallback(event: NuiVprEvent) {
        Log.i(TAG, "onNuiVprEventCallback event $event")
    }
}