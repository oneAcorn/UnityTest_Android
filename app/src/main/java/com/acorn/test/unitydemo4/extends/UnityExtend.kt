package com.acorn.test.unitydemo4.extends

import androidx.fragment.app.FragmentManager
import com.acorn.test.unitydemo4.widget.BaseRightDialog
import com.unity3d.player.UnityPlayer

/**
 * Created by acorn on 2021/7/24.
 */

fun BaseRightDialog.showDialog(
    fragmentManager: FragmentManager,
    dismissCallback: (() -> Unit)? = null
) {
    this.dismissCallback = {
        UnityPlayer.UnitySendMessage("model", "ResetScreen", "")
        dismissCallback?.invoke()
    }
    show(fragmentManager, this::class.java.simpleName)
    UnityPlayer.UnitySendMessage("model", "MoveToScreenLeft", "")
}