package com.teamvpn.devhub

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import com.teamvpn.devhub.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.username
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()
        login_button.setOnClickListener {
            if(!username.text.isNullOrBlank()) {
                if (!password.text.isNullOrBlank()) {
                    attemptToLogin()
                } else {
                    Toasty.warning(
                        this@LoginActivity,
                        "You cannot leave the password empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toasty.warning(this@LoginActivity,"Username/password is not provided",Toast.LENGTH_SHORT).show()
            }
        }

        // intent to register activity if user clicks on the register button
        signup_redirect.setOnClickListener {
            Log.d("DEBUG","User choose to register himself")
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }

    }

    private fun attemptToLogin(){
        Log.d("DEBUG","User choose to register himself")
        auth.signInWithEmailAndPassword(username.text.toString(), password.text.toString()).addOnCompleteListener(this, OnCompleteListener { task ->
            if(task.isSuccessful) {
                Toasty.success(this, "welcome, you are logged in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Toasty.error(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })

    }


    }


