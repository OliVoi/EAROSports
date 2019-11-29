package com.example.aerosports

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.aerosports.base.BaseActivity
import com.example.aerosports.customview.StatusBarView
import com.example.aerosports.mainui.dialog.ScannerBluetoothDialog
import com.example.aerosports.utils.Constant
import com.example.aerosports.utils.StringUtils
import com.jakewharton.rx.ReplayingShare
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener, StatusBarView.CallBackActionBar,
    ScannerBluetoothDialog.CallBackConnectDevice {
    private val REQUEST_ENABLE_BT: Int = 197
    private var mRxBleClient: RxBleClient? = null
    private var mRxDevice: RxBleDevice? = null
    private var mDisposable: Disposable? = null
    private var mRxConnection: RxBleConnection? = null

    private lateinit var characteristicUuid: UUID
    private val disconnectTriggerSubject = PublishSubject.create<Unit>()
    private lateinit var connectionObservable: Observable<RxBleConnection>
    private val connectionDisposable = CompositeDisposable()
    private lateinit var bleDevice: RxBleDevice

    var mBluetoothAdapter: BluetoothAdapter? = null
    var mIsConnected: Boolean = false
    var mDevice: BluetoothDevice? = null

    private fun prepareConnectionObservable(): Observable<RxBleConnection> =
        bleDevice.let {
            it!!.establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(ReplayingShare.instance())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNoActionBar()
        initView()
    }

    private fun initView() {
        topBar.setClickAction(this)
        btnStart.setOnClickListener(this)
    }

    private fun checkStatusBluetooth(): Boolean {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            StringUtils.reportMessage(this, getString(R.string.app_bluetooth_not_support))
            return false
        }
        if (!mBluetoothAdapter?.isEnabled!!) {
            StringUtils.reportMessage(this, getString(R.string.app_bluetooth_disable))
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return false
        }
        return true
    }

    private fun checkPermissions(): Boolean {
        var isPer: Boolean = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Only ask for these permissions on runtime when running Android 6.0 or higher
            when (ContextCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )) {
                PackageManager.PERMISSION_DENIED -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        197
                    )
                    isPer = false
                }
                PackageManager.PERMISSION_GRANTED -> {
                    isPer = true
                }
            }
        }
        return isPer
    }

    private fun witer() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            REQUEST_ENABLE_BT -> onActionLeft()
        }
    }

    override fun onBackPressed() {
        if (twoStepCloseApp()) super.onBackPressed()
    }

    override fun onActionRight() {
        if (isFinishing || !checkStatusBluetooth()) return
        disconnect()
    }

    override fun onActionLeft() {
        if (isFinishing || !checkStatusBluetooth() || !checkPermissions()) return
        val dialog = ScannerBluetoothDialog(this)
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeTranslucentNoStatus)
        dialog.show(supportFragmentManager, "Scanning")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStart -> onWriteClick()
        }
    }

    override fun onclickConnect(item: BluetoothDevice) {
        mDevice = item

        mRxBleClient = RxBleClient.create(this)
        bleDevice = mRxBleClient!!.getBleDevice(mDevice!!.address)
        connectionObservable = prepareConnectionObservable()
        connectionObservable
            .flatMapSingle { it.discoverServices() }
            .flatMapSingle { it.getCharacteristic(Constant.mMyUUID) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                Log.i(javaClass.simpleName, "Connecting...")
            }
            .subscribe(
                {
                    Log.i(javaClass.simpleName, "Hey, connection has been established!")
                },
                {
                    Log.i(javaClass.simpleName, "error!")
                },
                {
                    Log.i(javaClass.simpleName, "ok!")
                }
            )
            .let { connectionDisposable.add(it) }

        // connect ok
//        mRxBleClient = RxBleClient.create(this)
//        bleDevice = mRxBleClient!!.getBleDevice(mDevice!!.address)
//        bleDevice.observeConnectionStateChanges()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                Log.i(javaClass.simpleName, "Connecting...")
//            }
//            .let { mDisposable = it }
//
//        bleDevice!!.establishConnection(false)
//            .observeOn(AndroidSchedulers.mainThread())
//            .doFinally {
//                Log.i(javaClass.simpleName, "Connecting...")
//            }
//            .subscribe({
//                Log.i(javaClass.simpleName, "Connecting...")
//            }, {
//                onConnectionFailure(it)
//            })
//            .let { mDisposable = it }

//        mRxDevice = mRxBleClient?.getBleDevice(mDevice!!.address)
//        connectionObservable = prepareConnectionObservable()
//        onConnectToggleClick()

//        mRxDevice = mRxBleClient?.getBleDevice(mDevice!!.address)
//        mDisposable = mRxDevice?.establishConnection(false)?.subscribe(
//            Consumer {
//                mRxConnection = it
//                Log.e("", "")
//            }, Consumer {
//                Log.e("", "")
//            }
//        )
    }

    private fun onConnectToggleClick() {
        connectionObservable
            .flatMapSingle { it.discoverServices() }
            .flatMapSingle { it.getCharacteristic(Constant.mMyUUID) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { Log.i(javaClass.simpleName, "Connecting...") }
            .subscribe(
                {
                    Log.i(javaClass.simpleName, "Hey, connection has been established!")
                },
                {
                    onConnectionFailure(it)
                },
                {
                    Log.i(javaClass.simpleName, "gì đó!")
                }
            )
            .let { connectionDisposable.add(it) }
    }

    private fun onWriteClick() {
        connectionObservable
            .firstOrError()
            .flatMap { it.writeCharacteristic(characteristicUuid, "hihi".toByteArray()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i(javaClass.simpleName, "write ok")
            }, {
                Log.i(javaClass.simpleName, "write error")
            })
            .let { connectionDisposable.add(it) }
    }

    private fun onConnectionFailure(throwable: Throwable) {
        Log.i(javaClass.simpleName, "error!" + throwable.message)
    }

    private fun disconnect() {
        mDisposable.let { it?.dispose() }
    }
}
