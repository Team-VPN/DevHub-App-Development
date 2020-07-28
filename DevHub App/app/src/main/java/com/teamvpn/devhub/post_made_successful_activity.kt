package com.teamvpn.devhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import com.teamvpn.devhub.MainActivity.Companion.vibrator
import kotlinx.android.synthetic.main.activity_post_made_successful_activity.*

class post_made_successful_activity : AppCompatActivity() {
    var gif_rotator : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_made_successful_activity)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val app_preferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Get the value for the run counter

        // Get the value for the run counter
        gif_rotator = app_preferences.getInt("gif_rotator", 0)
        if(gif_rotator>=0 && gif_rotator<=2){
            gifImageView2.visibility = View.VISIBLE
            gifImageView3.visibility = View.INVISIBLE
            gifImageView4.visibility = View.INVISIBLE
        }else{
            if (gif_rotator>2 && gif_rotator<=4){
                gifImageView2.visibility = View.INVISIBLE
                gifImageView3.visibility = View.VISIBLE
                gifImageView4.visibility = View.INVISIBLE
            }
            else{
                if (gif_rotator>4 && gif_rotator<=6){
                    gifImageView2.visibility = View.INVISIBLE
                    gifImageView3.visibility = View.INVISIBLE
                    gifImageView4.visibility = View.VISIBLE
                }
                else{
                    gif_rotator = 0
                }
            }
        }
        // button to go back to previous activity
        button5.setOnClickListener{
            vibrator.vibrate(60)
            gif_rotator+=1
            Log.d("Gif rotator","Check"+gif_rotator)

            val app_preferences =
                PreferenceManager.getDefaultSharedPreferences(this)

            val editor = app_preferences.edit()
            editor.putInt("gif_rotator", gif_rotator)
            editor.commit()
            finish()
        }
    }
}