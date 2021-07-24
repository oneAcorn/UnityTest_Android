package com.acorn.test.unitydemo4.extends

import com.acorn.test.unitydemo4.bean.ResultObject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException

/**
 * Created by acorn on 2020/9/15.
 */

/**
 * 一般网络请求的调用链
 */
fun <T> Observable<T>.commonRequest(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun ResultObject<*>.checkIsSuccess() {
    if (code != 200) {
        throw RuntimeException("err($code):$msg")
    }
}