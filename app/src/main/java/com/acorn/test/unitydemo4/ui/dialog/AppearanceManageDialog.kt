package com.acorn.test.unitydemo4.ui.dialog

import com.acorn.test.unitydemo4.R
import com.acorn.test.unitydemo4.widget.BaseRightDialog
import com.unity3d.player.UnityPlayer
import kotlinx.android.synthetic.main.dialog_appearance_manage.*

/**
 * Created by acorn on 2021/7/24.
 */
class AppearanceManageDialog:BaseRightDialog() {
    private var curHair = 0
    private var curJacket = 0
    private var curTrousers = 0

    override fun getLayoutId(): Int {
        return R.layout.dialog_appearance_manage
    }

    override fun initData() {
    }

    override fun initAction() {
        hairBtn.setOnClickListener {
            curHair = if (curHair == 0) 1 else 0
            UnityPlayer.UnitySendMessage("model", "ChangeDigitalHumanTexture", "0,$curHair")
        }

        jacketBtn.setOnClickListener {
            curJacket = if (curJacket == 0) 1 else 0
            UnityPlayer.UnitySendMessage("model", "ChangeDigitalHumanTexture", "1,$curJacket")
        }

        trousersBtn.setOnClickListener {
            curTrousers = if (curTrousers == 0) 1 else 0
            UnityPlayer.UnitySendMessage("model", "ChangeDigitalHumanTexture", "2,$curTrousers")
        }
    }
}