package com.acorn.test.unitydemo4

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.acorn.test.unitydemo4.asr.AsrHelper
import com.acorn.test.unitydemo4.asr.IAsrListener
import com.acorn.test.unitydemo4.bean.AliyunTokenBean
import com.acorn.test.unitydemo4.extends.commonRequest
import com.acorn.test.unitydemo4.kws.BaiduKwsHelper
import com.acorn.test.unitydemo4.kws.IBaiduKwsListener
import com.acorn.test.unitydemo4.tts.ITtsListener
import com.acorn.test.unitydemo4.tts.TtsHelper
import com.acorn.test.unitydemo4.utils.network.HttpService
import com.acorn.test.unitydemo4.utils.network.RetrofitUtil
import com.acorn.test.unitydemo4.utils.MyConstants
import com.acorn.test.unitydemo4.utils.Utils
import com.acorn.test.unitydemo4.utils.logI
import com.unity3d.player.IUnityPlayerLifecycleEvents
import com.unity3d.player.UnityPlayer
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_unity_player.*

/**
 * Created by acorn on 2021/7/16.
 */
class TestActivity : AppCompatActivity(), IUnityPlayerLifecycleEvents, IAsrListener,
    IBaiduKwsListener, ITtsListener {
    protected lateinit var mUnityPlayer: UnityPlayer // don't change the name of this variable; referenced from native code

    private var curHair = 0
    private var curJacket = 0
    private var curTrousers = 0
    private val newModelName = "model2"
    private var isNewModelWalking = false
    private val ttsHelper = TtsHelper(this, lifecycle, this)
    private val asrHelper = AsrHelper(this, this)
    private val kwsHelper = BaiduKwsHelper(this, this)

    //小蜂回应呼叫的tts taskId
    private var respondTaskId: String = ""

    private val delayHandler = Handler(Looper.getMainLooper())

    // Override this in your custom UnityPlayerActivity to tweak the command line arguments passed to the Unity Android Player
    // The command line arguments are passed as a string, separated by spaces
    // UnityPlayerActivity calls this from 'onCreate'
    // Supported: -force-gles20, -force-gles30, -force-gles31, -force-gles31aep, -force-gles32, -force-gles, -force-vulkan
    // See https://docs.unity3d.com/Manual/CommandLineArguments.html
    // @param cmdLine the current command line arguments, may be null
    // @return the modified command line string or null
    protected fun updateUnityCommandLineArguments(cmdLine: String?): String? {
        return cmdLine
    }

    // Setup activity layout
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        val cmdLine = updateUnityCommandLineArguments(intent.getStringExtra("unity"))
        intent.putExtra("unity", cmdLine)

        val rootLayout =
            LayoutInflater.from(this)
                .inflate(R.layout.activity_unity_player, null, false) as ConstraintLayout
        mUnityPlayer = UnityPlayer(this, this)
        rootLayout.addView(
            mUnityPlayer,
            1,
            ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setContentView(rootLayout)
        mUnityPlayer!!.requestFocus()

//        val surfaceView = findViewById("unitySurfaceView") as SurfaceView
//        surfaceView.setBackgroundColor(Color.argb(0x00, 0, 0, 0))
//        surfaceView.holder.setFormat(PixelFormat.TRANSPARENT)
//        surfaceView.holder.addCallback(object : SurfaceHolder.Callback{
//            override fun surfaceCreated(holder: SurfaceHolder) {
////                Toast.makeText(this@TestActivity, "surfaceCreated", Toast.LENGTH_SHORT).show()
////                UnityPlayer.UnitySendMessage("Main Camera", "GlClear", "")
//            }
//
//            override fun surfaceChanged(
//                holder: SurfaceHolder,
//                format: Int,
//                width: Int,
//                height: Int
//            ) {
//                Toast.makeText(this@TestActivity, "surfaceChanged", Toast.LENGTH_SHORT).show()
//                UnityPlayer.UnitySendMessage("Main Camera", "GlClear", "")
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//            }
//
//        })

        requestToken()
        initListener()
    }

    private fun findViewById(id: String): View {
        val resId = resources.getIdentifier(id, "id", packageName);
        return findViewById(resId)
    }

    private fun initListener() {
        standBtn.setOnClickListener {
            //第一个参数是Unity中一个节点对象的名字，第二个参数是节点对象上挂的脚本中一个函数的名字，第三个参数是函数中的参数值
            UnityPlayer.UnitySendMessage("model", "ChangeStand", "")
        }
        walkBtn.setOnClickListener {
            //第一个参数是Unity中一个节点对象的名字，第二个参数是节点对象上挂的脚本中一个函数的名字，第三个参数是函数中的参数值
            UnityPlayer.UnitySendMessage("model", "ChangeWalk", "")
        }

        changeHairBtn.setOnClickListener {
            curHair = if (curHair == 0) 1 else 0
            UnityPlayer.UnitySendMessage("model", "ChangeDigitalHumanTexture", "0,$curHair")
        }

        changeJacketBtn.setOnClickListener {
            curJacket = if (curJacket == 0) 1 else 0
            UnityPlayer.UnitySendMessage("model", "ChangeDigitalHumanTexture", "1,$curJacket")
        }

        changeTrousersBtn.setOnClickListener {
            curTrousers = if (curTrousers == 0) 1 else 0
            UnityPlayer.UnitySendMessage("model", "ChangeDigitalHumanTexture", "2,$curTrousers")
        }

        addModelBtn.setOnClickListener {
            controlNewModelBtn.visibility = View.VISIBLE
            UnityPlayer.UnitySendMessage("Main Camera", "AddModel", newModelName)
        }

        deleteModelBtn.setOnClickListener {
            controlNewModelBtn.visibility = View.GONE
            UnityPlayer.UnitySendMessage("Main Camera", "DestroyModel", "")
        }

        controlNewModelBtn.setOnClickListener {
            if (isNewModelWalking) {
                UnityPlayer.UnitySendMessage(newModelName, "ChangeStand", "")
                UnityPlayer.UnitySendMessage(newModelName, "ChangeDigitalHumanTexture", "0,0")
                UnityPlayer.UnitySendMessage(newModelName, "ChangeDigitalHumanTexture", "1,0")
                UnityPlayer.UnitySendMessage(newModelName, "ChangeDigitalHumanTexture", "2,0")
            } else {
                UnityPlayer.UnitySendMessage(newModelName, "ChangeWalk", "")
                UnityPlayer.UnitySendMessage(newModelName, "ChangeDigitalHumanTexture", "0,1")
                UnityPlayer.UnitySendMessage(newModelName, "ChangeDigitalHumanTexture", "1,1")
                UnityPlayer.UnitySendMessage(newModelName, "ChangeDigitalHumanTexture", "2,1")
            }
            isNewModelWalking = !isNewModelWalking
        }

        ttsBtn.setOnClickListener {
            ttsHelper.startTts("""<speak>请闭上眼睛休息两秒<break time="2000ms"/>好了，请睁开眼睛。</speak>""")
        }

        startListenBtn.setOnClickListener {
            asrHelper.startDialog()
        }

        stopListenBtn.setOnClickListener {
            asrHelper.stopDialog()
        }
    }

    private fun requestToken() {
        RetrofitUtil.instance.create(HttpService::class.java)
            .getAliyunToken()
            .commonRequest()
            .subscribe(object : Observer<AliyunTokenBean> {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(t: AliyunTokenBean?) {
                    if (t?.code == "200" && t.data?.isNotEmpty() == true) {//success
                        logI("获取token成功,初始化")
                        MyConstants.NUI_TOKEN = t.data
                        ttsHelper.init()
                        asrHelper.init()
                        kwsHelper.init()
                        kwsHelper.start()
                    } else {
                        Toast.makeText(
                            this@TestActivity,
                            "获取token失败:${t?.code},${t?.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(e: Throwable?) {
                    Toast.makeText(
                        this@TestActivity,
                        "获取token失败:${e?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onComplete() {
                }

            })
    }

    // When Unity player unloaded move task to background
    override fun onUnityPlayerUnloaded() {
        moveTaskToBack(true)
    }

    // Callback before Unity player process is killed
    override fun onUnityPlayerQuitted() {}
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent)
        mUnityPlayer!!.newIntent(intent)
    }

    // Quit Unity
    override fun onDestroy() {
        mUnityPlayer!!.destroy()
        super.onDestroy()
    }

    // Pause Unity
    override fun onPause() {
        super.onPause()
        mUnityPlayer!!.pause()
    }

    // Resume Unity
    override fun onResume() {
        super.onResume()
        mUnityPlayer!!.resume()
    }

    // Low Memory Unity
    override fun onLowMemory() {
        super.onLowMemory()
        mUnityPlayer!!.lowMemory()
    }

    // Trim Memory Unity
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer!!.lowMemory()
        }
    }

    // This ensures the layout will be correct.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mUnityPlayer!!.configurationChanged(newConfig)
    }

    // Notify Unity of the focus change.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mUnityPlayer!!.windowFocusChanged(hasFocus)
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_MULTIPLE) mUnityPlayer!!.injectEvent(
            event
        ) else super.dispatchKeyEvent(event)
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    /*API12*/
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    override fun onListenStateChanged(isListening: Boolean) {
        startListenBtn.isEnabled = !isListening
        stopListenBtn.isEnabled = isListening
    }

    override fun onListenResult(str: String?) {
        str ?: return
        ttsHelper.startTts(str)
    }

    override fun onAsrError(err: String?, code: Int?) {
        startListenBtn.isEnabled = false
    }

    override fun onKwsResult(word: String) {
        respondTaskId = Utils.getRandomUUID32()
        ttsHelper.startTts("哎,你说", respondTaskId)
    }

    override fun onKwsError(err: String?, code: Int?) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
    }

    override fun onTtsVoiceEnd(taskId: String) {
        logI("onTtsVoiceEnd:$taskId,respondId:$respondTaskId")
        if (taskId == respondTaskId) { //小蜂回应用户,等待用户命令
            asrHelper.startDialog()
//            delayHandler.postDelayed({
//                asrHelper.startDialog()
//            }, 200)
        }
    }
}