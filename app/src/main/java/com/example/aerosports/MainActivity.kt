package com.example.aerosports

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
    private var mDisposable: Disposable? = null
    private val disconnectTriggerSubject = PublishSubject.create<Unit>()
    private lateinit var connectionObservable: Observable<RxBleConnection>
    private val connectionDisposable = CompositeDisposable()
    private lateinit var bleDevice: RxBleDevice
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mDevice: BluetoothDevice? = null
    private var mIsConect: Boolean = false
    private fun triggerDisconnect() = disconnectTriggerSubject.onNext(Unit)

    // param write
    private var mStartStop: Int = 0
    private var mRandom: Int = 0
    private var mLine2: Int = 0
    private var mLine3: Int = 0
    private var mSpeed: Int = 0
    private var mSpin: Int = 0
    private var mSpinValue: Int = 0
    private var mFeed: Int = 10
    private var mElev: Int = 0

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
        btnRandom.setOnClickListener(this)
        btnLineTwo.setOnClickListener(this)
        btnLineThree.setOnClickListener(this)
        imPlusSpeed.setOnClickListener(this)
        imMinusSpeed.setOnClickListener(this)
        imPlusSpin.setOnClickListener(this)
        imMinusSpin.setOnClickListener(this)
        imPlusFeed.setOnClickListener(this)
        imMinusFeed.setOnClickListener(this)
        imPlusElev.setOnClickListener(this)
        imMinusElev.setOnClickListener(this)
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
            when (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PackageManager.PERMISSION_DENIED -> { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 197)
                    isPer = false
                }
                PackageManager.PERMISSION_GRANTED -> {
                    isPer = true
                }
            }
        }
        return isPer
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStart -> submitAction(EnumAction.ON_OFF, false)
            R.id.btnLineTwo -> submitAction(EnumAction.LINE_2, false)
            R.id.btnLineThree -> submitAction(EnumAction.LINE_3, false)
            R.id.btnRandom -> submitAction(EnumAction.RANDOM, false)
            R.id.imMinusSpeed -> submitAction(EnumAction.SPEED, false)
            R.id.imPlusSpeed -> submitAction(EnumAction.SPEED, true)
            R.id.imPlusSpin -> submitAction(EnumAction.SPIN, true)
            R.id.imMinusSpin -> submitAction(EnumAction.SPIN, false)
            R.id.imPlusFeed -> submitAction(EnumAction.FEED, true)
            R.id.imMinusFeed -> submitAction(EnumAction.FEED, false)
            R.id.imPlusElev -> submitAction(EnumAction.ELEV, true)
            R.id.imMinusElev -> submitAction(EnumAction.ELEV, false)
            R.id.status -> onReadClick()
        }
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
        if (isFinishing || !checkStatusBluetooth() || !mIsConect) return
        triggerDisconnect()
    }

    override fun onActionLeft() {
        if (isFinishing || !checkStatusBluetooth() || !checkPermissions() || mIsConect) return
        val dialog = ScannerBluetoothDialog(this)
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeTranslucentNoStatus)
        dialog.show(supportFragmentManager, "Scanning")
    }

    private fun checkConnected(): Boolean{
        if (!mIsConect) StringUtils.reportMessage(this, getString(R.string.app_bluetooth_no_device))
        return mIsConect
    }

    override fun onclickConnect(item: BluetoothDevice) {
        mDevice = item
        tvDevice.text = mDevice!!.name
        mRxBleClient = RxBleClient.create(this)
        bleDevice = mRxBleClient!!.getBleDevice(mDevice!!.address)
        connectionObservable = prepareConnectionObservable()
        connectionObservable
            .flatMapSingle { it.discoverServices() }
            .flatMapSingle { it.getCharacteristic(Constant.mMyUUID) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                Log.i(javaClass.simpleName, "Connecting...")
                status.text = getString(R.string.app_bluetooth_connecting)
            }
            .subscribe(
                {
                    mIsConect = true
                    topBar.setImageRight(getDrawable(R.drawable.ic_plugin_connected))
                    Log.i(javaClass.simpleName, "Connected!")
                    status.text = getString(R.string.app_bluetooth_connected)
                },
                {
                    mIsConect = false
                    status.text = it.message
                    Log.i(javaClass.simpleName, "error!")
                },
                {
                    mIsConect = false
                    topBar.setImageRight(getDrawable(R.drawable.ic_plugin_out))
                    Log.i(javaClass.simpleName, "disconect ok!")
                    status.text = getString(R.string.app_bluetooth_disconnect)
                    finish()
                    startActivity(intent)
                }
            )
            .let { connectionDisposable.add(it) }
    }

    private fun onWriteClick(terminate: String) {
        connectionObservable
            .firstOrError()
            .flatMap { it.writeCharacteristic(Constant.mMyUUID, terminate.toByteArray()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i(javaClass.simpleName, "write ok")
            }, {
                Log.i(javaClass.simpleName, "write error")
            })
            .let { connectionDisposable.add(it) }
    }

    private fun onReadClick() {
        if (!checkConnected()) return
            connectionObservable
                .firstOrError()
                .flatMap { it.readCharacteristic(Constant.mMyUUID) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i(javaClass.simpleName, "Read ok")
                }, {
                    Log.i(javaClass.simpleName, "Read fail")
                })
                .let { connectionDisposable.add(it) }
    }

    private fun submitAction(enumAction: EnumAction, isPlus: Boolean) {
        if (!checkConnected()) return
        when (enumAction) {
            //On / off
            EnumAction.ON_OFF -> {
                mStartStop = if (mStartStop == 1) 0 else 1
                btnStart.text = if (mStartStop == 1) "Stop" else "Start"
                btnStart.setBackgroundResource(if (mStartStop == 1) R.drawable.bgr_btn_red else R.drawable.bgr_btn_start)
                onWriteClick(StringUtils.baseStop + mStartStop)
            }
            // Random
            EnumAction.RANDOM -> {
                var image = 0
                mRandom += 1
                if (mRandom > 5) mRandom = 0
                if (mRandom == 0) image = R.drawable.ic_dot_0
                if (mRandom == 1) image = R.drawable.ic_dot_1
                if (mRandom == 2) image = R.drawable.ic_dot_2
                if (mRandom == 3) image = R.drawable.ic_dot_3
                if (mRandom == 4) image = R.drawable.ic_dot_4
                if (mRandom == 5) image = R.drawable.ic_dot_5
                imRandom.setImageResource(image)
                onWriteClick(StringUtils.baseRandom + mRandom)
            }
            // Line 2
            EnumAction.LINE_2 -> actionLine(2)
            // Line 3
            EnumAction.LINE_3 -> actionLine(3)
            // Speed
            EnumAction.SPEED -> {
                if ((isPlus && mSpeed == 20) || (!isPlus && mSpeed == 0)) return
                mSpeed = if (isPlus) mSpeed + 1 else mSpeed - 1
                tvSpeed.text = mSpeed.toString()
                onWriteClick(StringUtils.baseSpeed + mSpeed)
            }
            // Spin
            EnumAction.SPIN -> {
                if ((isPlus && mSpin == 20) || (!isPlus && mSpin == 1)) return
                mSpinValue = if (isPlus) mSpinValue + 1 else mSpinValue - 1
//                if (isPlus && mSpinValue == 0) {
//                    mSpinValue += 1
//                }
//                if (!isPlus && mSpinValue == 0) {
//                    mSpinValue -= 1
//                }
                if (isPlus && mSpin != 0) {
                    mSpin ++
                }
                if (!isPlus && mSpin != 0) {
                    mSpin --
                }
                if (isPlus && mSpin == 0) {
                    mSpin += 11
                }
                if (!isPlus && mSpin == 0) {
                    mSpin += 10
                }
                if (mSpinValue == 0) mSpin = 0
                tvSpin.text = mSpinValue.toString()
                onWriteClick(StringUtils.baseSpin + mSpin)
            }
            // Feed
            EnumAction.FEED -> {
                if ((isPlus && mFeed == 2) || (!isPlus && mFeed == 10)) return
                mFeed = if (isPlus) mFeed - 1 else mFeed + 1
                tvFeed.text = if (mFeed == 10) "off" else mFeed.toString() + "s"
                onWriteClick(StringUtils.baseFeed + (10 - mFeed))
            }
            // Elev
            EnumAction.ELEV -> {
                if ((isPlus && mElev == 1) || (!isPlus && mElev == 0)) {
                    onWriteClick(StringUtils.baseElev + mElev)
                    return
                }
                mElev = if (isPlus) mElev + 1 else mElev - 1
                tvElev.text = mElev.toString()
                onWriteClick(StringUtils.baseElev + mElev)
            }
        }
    }

    private fun actionLine(typeLine: Int) {
        when (typeLine) {
            2 -> {
                mLine3 = 0
                mLine2 = if (mLine2 == 0) 1 else 0
                btnLineThree.setBackgroundResource(R.color.white)
                btnLineTwo.setBackgroundResource(if (mLine2 != 0) R.drawable.bgr_selected else R.color.white)
                onWriteClick(StringUtils.baseLine2 + mLine2)
            }
            3 -> {
                mLine2 = 0
                mLine3 = if (mLine3 == 0) 1 else 0
                btnLineTwo.setBackgroundResource(R.color.white)
                btnLineThree.setBackgroundResource(if (mLine3 != 0) R.drawable.bgr_selected else R.color.white)
                onWriteClick(StringUtils.baseLine3 + mLine3)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mDevice == null || mIsConect) return
        onclickConnect(mDevice!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {197 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onActionLeft()
                }
            }
        }
    }
//
//
//    override fun onPause() {
//        super.onPause()
//        connectionDisposable.clear()
//    }
}
