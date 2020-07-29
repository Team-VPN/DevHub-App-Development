package com.teamvpn.devhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teamvpn.devhub.MainActivity.Companion.vibrator
import kotlinx.android.synthetic.main.activity_post_made_successful_activity.*

class post_made_successful_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_made_successful_activity)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // button to go back to previous activity
        button5.setOnClickListener{
            vibrator.vibrate(60)
            
            finish()
        }
    }
}