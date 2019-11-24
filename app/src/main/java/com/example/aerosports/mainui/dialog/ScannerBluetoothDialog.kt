package com.example.aerosports.mainui.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.aerosports.R
import kotlinx.android.synthetic.main.dialog_scanner.*

class ScannerBluetoothDialog: DialogFragment(), View.OnClickListener{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = inflater?.inflate(R.layout.dialog_scanner, container, false)
        return view!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView (){
        btnClose.setOnClickListener(this)
        tvScanning.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window!!.setWindowAnimations(R.style.DialogAnimationUpDown)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            dialog?.window!!.statusBarColor = resources.getColor(R.color.startColorPrimary)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnClose -> dismiss()
            R.id.tvScanning -> dismiss()
        }
    }
}