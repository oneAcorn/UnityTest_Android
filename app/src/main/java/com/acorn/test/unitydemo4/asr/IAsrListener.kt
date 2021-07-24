package com.acorn.test.unitydemo4.asr

/**
 * Created by acorn on 2021/7/21.
 */
interface IAsrListener {
    fun onAsrStateChanged(isListening: Boolean)

    fun onAsrResult(str: String?)

    fun onAsrError(err: String?, code: Int? = -1)
}