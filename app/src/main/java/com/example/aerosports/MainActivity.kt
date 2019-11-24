package com.example.aerosports

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.aerosports.base.BaseActivity
import com.example.aerosports.customview.StatusBarView
import com.example.aerosports.mainui.dialog.ScannerBluetoothDialog
import com.example.aerosports.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener, StatusBarView.CallBackActionBar {
    private val REQUEST_ENABLE_BT: Int = 197

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNoActionBar()
        initView()
    }

    private fun initView() {
        topBar.setClickAction(this)
    }

    private fun checkStatusBluetooth(): Boolean {
        val bluetooth = BluetoothAdapter.getDefaultAdapter()
        if (bluetooth == null) {
            StringUtils.reportMessage(this, getString(R.string.app_bluetooth_not_support))
            return false
        }
        if (!bluetooth.isEnabled) {
            StringUtils.reportMessage(this, getString(R.string.app_bluetooth_disable))
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when(requestCode){
            REQUEST_ENABLE_BT -> onActionLeft()
        }
    }

    override fun onBackPressed() {
        if (twoStepCloseApp()) super.onBackPressed()
    }

    override fun onActionRight() {
        if (isFinishing || !checkStatusBluetooth()) return
    }

    override fun onActionLeft() {
        if (isFinishing || !checkStatusBluetooth()) return
        val dialog = ScannerBluetoothDialog()
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeTranslucentNoStatus)
        dialog.show(supportFragmentManager, "Scanning")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}
