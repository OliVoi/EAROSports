package com.example.aerosports.customview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.aerosports.utils.StringUtils;

/**
 * Created by PPCLink on 22/03/2018.
 */

public class TextViewCSRegular extends AppCompatTextView {

    public TextViewCSRegular(Context context) {
        super(context);
    }

    public TextViewCSRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(StringUtils.setCustomFont(context, "fonts/S-Core - CoreSansGS45Regular1.ttf"));
    }

    public TextViewCSRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(StringUtils.setCustomFont(context, "fonts/S-Core - CoreSansGS45Regular1.ttf"));
    }

}