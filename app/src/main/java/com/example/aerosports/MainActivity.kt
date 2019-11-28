package com.example.aerosports

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
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
import com.example.aerosports.utils.Constant
import com.example.aerosports.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : BaseActivity(), View.OnClickListener, StatusBarView.CallBackActionBar,
    ScannerBluetoothDialog.CallBackConnectDevice {
    private val REQUEST_ENABLE_BT: Int = 197
    companion object {
        var mBluetoothAdapter: BluetoothAdapter? = null
        var mBluetoothSocket: BluetoothSocket? = null
        lateinit var mProgress: ProgressDialog
        var mIsConnected: Boolean = false
        var mDevice: BluetoothDevice? = null
    }

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
        val dialog = ScannerBluetoothDialog(this)
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeTranslucentNoStatus)
        dialog.show(supportFragmentManager, "Scanning")
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    override fun onclickConnect(item: BluetoothDevice) {
        ConnectToDevice(this).execute()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context = c

        override fun onPreExecute() {
            super.onPreExecute()
            mProgress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (mBluetoothSocket == null || !mIsConnected) {
                    val device: BluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(mDevice?.address)
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(Constant.mMyUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    mBluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                StringUtils.reportMessage(context, "couldn't connect")
            } else {
                mIsConnected = true
            }
            mProgress.dismiss()
        }
    }
}
