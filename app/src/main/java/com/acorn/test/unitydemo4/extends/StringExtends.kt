package com.acorn.test.unitydemo4.extends

import java.security.MessageDigest

/**
 * Created by acorn on 2021/7/20.
 */

/**
 * String MD5 转换
 */
fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.hex()
}

fun ByteArray.hex(): String {
    return joinToString("") { "%02X".format(it) }
}