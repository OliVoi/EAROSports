package com.example.aerosports.mainui.dialog

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aerosports.R
import kotlinx.android.synthetic.main.dialog_scanner.*

class ScannerBluetoothDialog(callBack: CallBackConnectDevice) : DialogFragment(),
    View.OnClickListener, CallBackDevice {
    private var mIsScanning = false
    private var mListDevice: MutableList<BluetoothDevice> = ArrayList()
    private var mDeviceAdapter: DeviceBluetoothAdapter? = null
    private val mCallBack: CallBackConnectDevice = callBack
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mRegisted: Boolean = false

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contextDevice: Context?, intentDevice: Intent?) {
            when (intentDevice!!.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    mListDevice.add(intentDevice!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
                    setupViewDevice()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = inflater?.inflate(R.layout.dialog_scanner, container, false)
        return view!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        btnClose.setOnClickListener(this)
        tvScanning.setOnClickListener(this)
        scanning()
    }

    private fun scanning() {
        mIsScanning = !mIsScanning
        if (mIsScanning) {
            mRegisted = true
            tvScanning.text = "Stop"
            mListDevice = ArrayList()
            ltAnimation.playAnimation()
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            mBluetoothAdapter?.startDiscovery()
            activity?.registerReceiver(mReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            return
        }
        mRegisted = false
        tvScanning.text = "Scan"
        ltAnimation.pauseAnimation()
        mBluetoothAdapter?.cancelDiscovery()
        activity?.unregisterReceiver(mReceiver)
    }

    private fun setupViewDevice() {
        if (mDeviceAdapter == null) {
            rcDevice.layoutManager = LinearLayoutManager(context)
            rcDevice.adapter = DeviceBluetoothAdapter(mListDevice, context!!, this)
            return
        }
        mDeviceAdapter?.updateData(mListDevice)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothAdapter?.cancelDiscovery()
        if (mRegisted) activity?.unregisterReceiver(mReceiver)
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
        when (v?.id) {
            R.id.btnClose -> dismiss()
            R.id.tvScanning -> scanning()
        }
    }

    interface CallBackConnectDevice {
        fun onclickConnect(item: BluetoothDevice)
    }

    override fun onclickItem(item: BluetoothDevice) {
        item.let {
            mCallBack.onclickConnect(item)
            dismiss()
        }
    }
}