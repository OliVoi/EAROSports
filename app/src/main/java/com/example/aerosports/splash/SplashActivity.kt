package com.example.aerosports.splash

import android.content.Intent
import android.os.Bundle
import com.example.aerosports.MainActivity
import com.example.aerosports.R
import com.example.aerosports.base.BaseActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setNoActionBar()

        Timer("Login", false).schedule(1000) {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
