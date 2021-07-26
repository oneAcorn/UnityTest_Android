package com.acorn.test.unitydemo4.bean

/**
 * 自然语言处理
 * Created by acorn on 2021/7/24.
 */
data class NLPBean(
    //1:说话,2:链接
    val type: String,
    val answer: String?,
    val url: String?
)