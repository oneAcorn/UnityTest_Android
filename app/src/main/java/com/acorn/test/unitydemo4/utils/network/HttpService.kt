package com.acorn.test.unitydemo4.utils.network

import com.acorn.test.unitydemo4.bean.AliyunTokenBean
import com.acorn.test.unitydemo4.bean.NLPBean
import com.acorn.test.unitydemo4.bean.ResultObject
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * Created by acorn on 2021/7/23.
 */
interface HttpService {
    @GET("shuziren/token")
    fun getAliyunToken(): Observable<AliyunTokenBean>

    @POST("shuziren/nlp")
    fun nlp(@Body map: Map<String, String>): Observable<ResultObject<NLPBean>>
}