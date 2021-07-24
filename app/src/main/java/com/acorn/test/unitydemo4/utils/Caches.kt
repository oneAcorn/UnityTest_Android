package com.acorn.test.unitydemo4.utils

import com.acorn.test.unitydemo4.extends.string
import com.tencent.mmkv.MMKV

/**
 * Created by acorn on 2021/7/24.
 */
object Caches {
    private val mmkv = MMKV.mmkvWithID("digitalHuman", MMKV.SINGLE_PROCESS_MODE)
    var curScene: String? by mmkv.string()
}