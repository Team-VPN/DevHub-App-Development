package com.teamvpn.devhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class about_us : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }

    override fun onBackPressed() {

        startActivity(Intent(this@about_us,MainActivity::class.java))
        finish()
    }
}