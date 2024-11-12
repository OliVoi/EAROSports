package com.example.aerosports.base

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import com.example.aerosports.R
import com.example.aerosports.utils.StringUtils

open class BaseActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var mOverlayDialog: Dialog? = null
    private var mIsCloseApp: Boolean = false

    fun setNoActionBar() {
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun twoStepCloseApp(): Boolean {
        if (mIsCloseApp) return mIsCloseApp
        mIsCloseApp = true
        StringUtils.reportMessage(this, getString(R.string.app_click_to_back))
        Handler().postDelayed({ mIsCloseApp = false }, 3000)
        return false
    }
}