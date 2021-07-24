package com.acorn.test.unitydemo4.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.acorn.test.unitydemo4.R
import com.acorn.test.unitydemo4.extends.dp
import com.acorn.test.unitydemo4.widget.BaseRightDialog
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.dialog_webview.*

/**
 * Created by acorn on 2021/7/24.
 */
class WebviewDialog(private val url:String) : BaseRightDialog() {
    private var webView: WebView? = null

    override fun getLayoutId(): Int {
        return R.layout.dialog_webview
    }

    override fun initData() {
        webView = WebView(context)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        container.addView(webView, layoutParams)
        initWebView()
        webView?.loadUrl(url)
    }

    override fun initAction() {
    }

    override fun getLayoutHeight(): Int {
        return 640.dp
    }

    override fun onDestroyView() {
        if (webView != null) {
            webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView?.clearHistory()
            webView?.clearCache(true)
            (webView?.parent as ViewGroup).removeView(webView)
            webView?.destroy()
        }
        super.onDestroyView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val webSettings = webView?.settings
        //设置WebView属性，能够执行Javascript脚本
        webSettings?.javaScriptEnabled = true
        //设置可以访问文件
        webSettings?.allowFileAccess = true
        webSettings?.setAllowFileAccessFromFileURLs(true)
        webSettings?.setAllowUniversalAccessFromFileURLs(true)
        //设置支持缩放
        webSettings?.builtInZoomControls = false
        //允许js弹出窗口
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        //设置自适应屏幕，两者合用将图片调整到适合webview的大小
        webSettings?.useWideViewPort = true
        // 缩放至屏幕的大小
        webSettings?.loadWithOverviewMode = true
        //关闭webview中缓存
        webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE
        //支持自动加载图片
        webSettings?.loadsImagesAutomatically = true
        //设置编码格式
        webSettings?.defaultTextEncodingName = "utf-8"
        //启用数据库
        webSettings?.databaseEnabled = true
        //设置定位的数据库路径
        val dir = activity?.applicationContext?.getDir("database", Context.MODE_PRIVATE)?.path
        webSettings?.setGeolocationDatabasePath(dir)
        //启用地理定位
        webSettings?.setGeolocationEnabled(true)
        //开启DomStorage缓存
        webSettings?.domStorageEnabled = true

        webView?.let {


            it.webViewClient = object : WebViewClient() {

                override fun onReceivedSslError(
                    p0: WebView?,
                    handler: com.tencent.smtt.export.external.interfaces.SslErrorHandler?,
                    p2: com.tencent.smtt.export.external.interfaces.SslError?
                ) {
//                    super.onReceivedSslError(p0, p1, p2)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webView?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    handler?.proceed()   //解决加载https报错问题
                }

                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                }
            }
        }
    }
}