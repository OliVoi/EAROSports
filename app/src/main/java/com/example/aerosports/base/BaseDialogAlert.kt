package com.example.aerosports.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class BaseDialogAlert(context: Context, title: String, content: String, callBack: CallBackAlert) :
    AlertDialog(context) {

    private val mTitle = title
    private val mContent = content
    private val mCallBack = callBack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(mTitle)
        setMessage(mContent)
        setButton(BUTTON_POSITIVE, "Yes") { dialog, which -> mCallBack.onClickYest() }
        setButton(DialogInterface.BUTTON_NEUTRAL, "No") { dialog, which -> dismiss() }
    }

    interface CallBackAlert {
        fun onClickYest()
    }
}