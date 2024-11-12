package com.example.aerosports.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.aerosports.utils.StringUtils
import com.example.aerosports.utils.StringUtils.Companion.setCustomFont

class TextViewCSRegular: AppCompatTextView{
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        typeface = setCustomFont(context, "fonts/S-Core - CoreSansGS45Regular1.ttf")
    }
}