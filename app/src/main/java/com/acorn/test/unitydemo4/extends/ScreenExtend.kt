package com.acorn.test.unitydemo4.extends

import android.content.res.Resources

/**
 * Created by acorn on 2021/7/24.
 */
// 屏幕宽高
val screenWidth get() = Resources.getSystem().displayMetrics.widthPixels
val screenHeight get() = Resources.getSystem().displayMetrics.heightPixels