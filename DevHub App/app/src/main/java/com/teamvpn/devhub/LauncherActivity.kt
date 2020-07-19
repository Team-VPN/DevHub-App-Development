package com.teamvpn.devhub

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener


class LauncherActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: AuthStateListener? = null
    internal val TIME_OUT = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()

        mAuthListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Handler().postDelayed(
                    {
                        Log.d("DEBUG","User is not logged in, so login activity is launched")
                        val intent = Intent(this@LauncherActivity, MainActivity::class.java)
                        Log.d("DEBUG","User is already logged in, so main activity is launched")
                        startActivity(intent)
                        finish()
                    },TIME_OUT.toLong())
            }else{
                // Wait for three seconds and move to main activity if login is successful, or else go for login/register activity
                Handler().postDelayed(
                    {
                        Log.d("DEBUG","User is not logged in, so login activity is launched")
                        startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
                        //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        finish()
                    },TIME_OUT.toLong()
                )
            }
        }

    }

    override fun onStart(){
        super.onStart()
        mAuthListener?.let { firebaseAuth?.addAuthStateListener(it) }
    }

    override fun onResume() {
        super.onResume()
        mAuthListener?.let { firebaseAuth?.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        mAuthListener?.let { firebaseAuth?.removeAuthStateListener (it) }
    }

}