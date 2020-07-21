package com.teamvpn.devhub

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password


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

        // when button is clicked, show the alert
        forgot_password_button.setOnClickListener {
            // build alert dialog
            val email = username.getText().toString()
            if (!username.text.isNullOrBlank()){
            val dialogBuilder = AlertDialog.Builder(this)
            // set message of alert dialog
            dialogBuilder.setMessage("Click on 'Continue' to Reset the Password")
                // if the dialog is cancelable
                .setCancelable(false)
                .setPositiveButton("Continue", DialogInterface.OnClickListener { dialog, id ->

                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toasty.success(
                                    this@LoginActivity,
                                    "Reset link sent to your email",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else {
                                Toasty.info(
                                    this@LoginActivity,
                                    "Unable to send re-enter mail",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })

                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Reset Password")
            // show alert dialog
            alert.show()
        }
            else{
                Toasty.info(this@LoginActivity, "Please enter your email", Toast.LENGTH_LONG).show()
            }
        }
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
                val user = auth.currentUser?.displayName
                Toasty.success(this, "welcome $user, you are signed in", Toast.LENGTH_SHORT).show()
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


