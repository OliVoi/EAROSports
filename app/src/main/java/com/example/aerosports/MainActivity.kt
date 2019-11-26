package com.example.aerosports

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private fun checkPermissions(): Boolean{
        var isPer: Boolean = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Only ask for these permissions on runtime when running Android 6.0 or higher
            when (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PackageManager.PERMISSION_DENIED ->{
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 197)
                    isPer = false
                }
                PackageManager.PERMISSION_GRANTED -> {
                    isPer = true
                }
            }
        }
        return isPer
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
        if (isFinishing || !checkStatusBluetooth() || !checkPermissions()) return
        val dialog = ScannerBluetoothDialog()
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeTranslucentNoStatus)
        dialog.show(supportFragmentManager, "Scanning")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}
