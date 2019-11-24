package com.example.aerosports

import android.os.Bundle
import com.example.aerosports.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNoActionBar()
    }

    override fun onBackPressed() {
        if (twoStepCloseApp()) super.onBackPressed()
    }
}
