package com.acorn.test.unitydemo4.kws

/**
 * Created by acorn on 2021/7/23.
 */
interface IBaiduKwsListener {

    fun onKwsResult(word:String)

    fun onKwsError(err: String?, code: Int? = -1)
}