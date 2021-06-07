package com.example.finalproject.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R


class SplashActivity : AppCompatActivity() {

    private val mWaitHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mWaitHandler.postDelayed(Runnable {
            try {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }
        }, 2000)

    }
}