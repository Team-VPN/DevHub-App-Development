package com.teamvpn.devhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class LauncherActivity : AppCompatActivity() {
    internal val TIME_OUT = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        supportActionBar?.hide()
        // Wait for three seconds and move to main activity if login is successful, or else go for login/register activity
        Handler().postDelayed(
            {
                startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                finish()
            },TIME_OUT.toLong()
        )

    }

}