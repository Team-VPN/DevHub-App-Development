package com.teamvpn.devhub

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import android.os.Bundle
import android.os.Vibrator
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
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import com.teamvpn.devhub.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.username
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    lateinit var vibrator:Vibrator
    private lateinit var auth: FirebaseAuth
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Hold your seat with patience...")
        progressDialog.setCanceledOnTouchOutside(false)

        login_button.setOnClickListener {

            if(!username.text.isNullOrBlank()) {
                if (!password.text.isNullOrBlank()) {
                    progressDialog.show()
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
            vibrator.vibrate(60)
            Log.d("DEBUG","User choose to register himself")
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

    }

    private fun attemptToLogin(){
        Log.d("DEBUG","User choose to register himself")
        auth.signInWithEmailAndPassword(username.text.toString(), password.text.toString()).addOnCompleteListener(this, OnCompleteListener { task ->
            if(task.isSuccessful) {
                Toasty.success(this, "welcome, you are logged in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                progressDialog.dismiss()
                startActivity(intent)
                finish()
            }else {
                progressDialog.dismiss()
                Toasty.error(this, "Hey! Login Failed, if you have an account, you can retrieve it by forgot password", Toast.LENGTH_SHORT).show()
            }
        })

    }


    }


