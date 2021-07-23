package com.acorn.test.unitydemo4.tts

import com.alibaba.idst.nui.INativeTtsCallback

/**
 * Created by acorn on 2021/7/23.
 */
interface ITtsListener {
    fun onTtsEvent(event: INativeTtsCallback.TtsEvent, taskId: String)
}