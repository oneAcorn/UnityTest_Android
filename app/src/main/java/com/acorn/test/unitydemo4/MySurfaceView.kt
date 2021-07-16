package com.acorn.test.unitydemo4

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Created by acorn on 2021/7/16.
 */
class MySurfaceView : SurfaceView,SurfaceHolder.Callback {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ){
        holder.addCallback(this)
    }

    private fun draw(){
        val canvas=holder.lockCanvas()
        canvas.drawARGB(255,30,20,120)
        canvas.drawBitmap(BitmapFactory.decodeResource(resources,R.drawable.app_banner),0f,0f,Paint().apply {
            isAntiAlias=true
        })
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        draw()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}