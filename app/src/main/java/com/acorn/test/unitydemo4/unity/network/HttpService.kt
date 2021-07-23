package com.acorn.test.unitydemo4.unity.network

import com.acorn.test.unitydemo4.bean.AliyunTokenBean
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

/**
 * Created by acorn on 2021/7/23.
 */
interface HttpService {
    @GET("shuziren/token")
    fun getAliyunToken(): Observable<AliyunTokenBean>
}