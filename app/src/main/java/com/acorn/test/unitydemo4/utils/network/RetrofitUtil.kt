package com.acorn.test.unitydemo4.utils.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by acorn on 2019-06-11.
 */
class RetrofitUtil private constructor() {
//    .addInterceptor { chain ->
//        var request = chain.request()
//        val builder = request.newBuilder()
//        request = builder.apply {
//            //在此添加header
////                    addHeader("version", PackageUtils.getAppVersion())
////                    addHeader("appChannel", PublicUtils.getApkChannel(Res.getContext()))
////                    addHeader("channel", Constant.FROM_ANDROID)
////                    addHeader("appName", Constant.appName)
////                    addHeader("Connection", "close")
//        }.build()
//        chain.proceed(request)
//    }
    private val client = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .build()

    companion object {
        const val CONNECT_TIME_OUT = 60
        const val READ_TIME_OUT = 60
        const val WRITE_TIME_OUT = 60

        val instance: Retrofit by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RetrofitUtil().getRetrofit() }
    }

    private fun getRetrofit(): Retrofit = Retrofit
            .Builder()
            .baseUrl(getBaseUrl()).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()


    private fun getBaseUrl(): String {
        //        return Env.UAT ? "http://10.103.11.173:8080/brokerservice-server/" :
        //                "http://mapi.sfbest.com/brokerservice-server/";
        return "http://172.16.110.170:8888/"
    }
}