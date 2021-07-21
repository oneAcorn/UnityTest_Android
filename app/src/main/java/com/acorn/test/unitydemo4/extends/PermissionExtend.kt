package com.acorn.test.unitydemo4.extends

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions3.RxPermissions

/**
 * Created by acorn on 2020/11/18.
 */

/**
 * @param allPermGrantedCallback 所有权限都同意时才回调
 * @param anyPermDeniedCallback 任意权限不同意时回调
 */
fun FragmentActivity.requestPermission(vararg permission: String,
                                       allPermGrantedCallback: (() -> Unit)? = null,
                                       anyPermDeniedCallback: (() -> Unit)? = null) {
    val disposable = RxPermissions(this).request(*permission)
            .subscribe { granted ->
                if (granted) {
                    allPermGrantedCallback?.invoke()
                } else {
                    anyPermDeniedCallback?.invoke()
                }
            }
}

/**
 * @param allPermGrantedCallback 所有权限都同意时才回调
 * @param anyPermDeniedCallback 任意权限不同意时回调
 */
fun Fragment.requestPermission(vararg permission: String,
                               allPermGrantedCallback: (() -> Unit)? = null,
                               anyPermDeniedCallback: (() -> Unit)? = null) {
    val disposable = RxPermissions(this).request(*permission)
            .subscribe { granted ->
                if (granted) {
                    allPermGrantedCallback?.invoke()
                } else {
                    anyPermDeniedCallback?.invoke()
                }
            }
}

fun Context.hasPermissions(vararg perms: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    for (perm in perms) {
        val hasPerm = ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        if (!hasPerm) {
            return false
        }
    }
    return true
}