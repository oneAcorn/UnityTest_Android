package com.acorn.test.unitydemo4.asr

/**
 * Created by acorn on 2021/7/21.
 */
interface IAsrListener {
    fun onListenStateChanged(isListening: Boolean)

    fun onListenResult(str: String?)

    fun onAsrError(err: String?, code: Int? = -1)
}