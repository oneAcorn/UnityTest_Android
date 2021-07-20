package com.acorn.test.unitydemo4.bean

/**
 * Created by acorn on 2021/7/20.
 */
data class TTSBean(
    val subtitles: List<Subtitle>
)

data class Subtitle(
    val begin_index: Int,
    val begin_time: Int,
    val end_index: Int,
    val end_time: Int,
//    val phoneme: String?,
    val text: String
)