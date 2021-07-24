package com.acorn.test.unitydemo4.widget

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.acorn.test.unitydemo4.R
import com.acorn.test.unitydemo4.extends.dp


/**
 * Created by acorn on 2020/8/7.
 */
abstract class BaseRightDialog : AppCompatDialogFragment() {
    var dismissCallback: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(getLayoutId(), null)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.RightDialog)

        dialog?.window?.run {
            requestFeature(Window.FEATURE_NO_TITLE);
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));//注意此处
            var uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  //布局位于状态栏下方
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            uiOptions = uiOptions or 0x00001000
            decorView.systemUiVisibility = uiOptions
//            decorView.setOnSystemUiVisibilityChangeListener {
//
//            }
            setLayout(getLayoutWidth(), getLayoutHeight());//这2行,和上面的一样,注意顺序就行;
            setGravity(Gravity.CENTER_VERTICAL or Gravity.END)
            setWindowAnimations(R.style.AnimationDialog)
            setBackgroundDrawableResource(R.color.transparent)
        }
//        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        initAction()
    }

    open fun getLayoutWidth() = 360.dp

    open fun getLayoutHeight() = WindowManager.LayoutParams.MATCH_PARENT

    abstract fun getLayoutId(): Int

    abstract fun initData()

    abstract fun initAction()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke()
    }
}