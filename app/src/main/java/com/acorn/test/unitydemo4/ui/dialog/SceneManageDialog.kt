package com.acorn.test.unitydemo4.ui.dialog

import android.content.Intent
import com.acorn.test.unitydemo4.R
import com.acorn.test.unitydemo4.TestActivity
import com.acorn.test.unitydemo4.utils.Caches
import com.acorn.test.unitydemo4.widget.BaseRightDialog
import kotlinx.android.synthetic.main.dialog_scene_manage.*

/**
 * Created by acorn on 2021/7/24.
 */
class SceneManageDialog : BaseRightDialog() {
    override fun getLayoutId(): Int {
        return R.layout.dialog_scene_manage
    }

    override fun initData() {
    }

    override fun initAction() {
        stewardBtn.setOnClickListener {
            changeScene("3")
        }
        triageBtn.setOnClickListener {
            changeScene("2")
        }
    }

    private fun changeScene(scene: String) {
        Caches.curScene = scene
        dismiss()
    }
}