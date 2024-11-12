package com.example.aerosports.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.example.aerosports.R
import kotlinx.android.synthetic.main.custom_action_bar.view.*

class StatusBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var mCall: CallBackActionBar? = null
    private var statusBarHeight = 0
    private var resourceId = 0
    private var mTitle = ""
    private val mImageRight: Drawable?;
    private val mImageLeft: Drawable?;

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_action_bar, this)

        resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) statusBarHeight = getResources().getDimensionPixelSize(resourceId)
        headerActionBar.setPadding(0, statusBarHeight, 0, 0)

        var typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView, 0, 0)
        imgLeft.setOnClickListener(this)
        imgRight.setOnClickListener(this)

        mTitle = typedArray.getString(R.styleable.StatusBarView_action_text).toString()
        mImageLeft = typedArray.getDrawable(R.styleable.StatusBarView_action_icon_left)
        mImageRight = typedArray.getDrawable(R.styleable.StatusBarView_action_icon_right)

        setTitle(mTitle)
        setImageLeft(mImageLeft)
        setImageRight(mImageRight)
    }

    fun setTitle(title: String) {
        if (TextUtils.isEmpty(title)) return
        tvTitle.setText(title.trim())
    }

    fun setImageLeft(iconLeft: Drawable?) {
        if (iconLeft == null) return
        imgLeft.setImageDrawable(iconLeft)
        imgLeft.visibility = View.VISIBLE
    }

    fun setImageRight(iconRight: Drawable?) {
        if (iconRight == null) return
        imgRight.setImageDrawable(iconRight)
        imgRight.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgLeft -> mCall?.onActionLeft()
            R.id.imgRight -> mCall?.onActionRight()
        }
    }

    fun setClickAction(action: CallBackActionBar) {
        this.mCall = action
    }

    interface CallBackActionBar {
        fun onActionRight()
        fun onActionLeft()
    }
}