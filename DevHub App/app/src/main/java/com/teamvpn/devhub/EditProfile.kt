package com.teamvpn.devhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.teamvpn.devhub.MainActivity.Companion.database
import com.teamvpn.devhub.ModelClass.userss
import kotlinx.android.synthetic.main.activity_edit_profile.*

import kotlinx.android.synthetic.main.activity_main.*

class EditProfile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar)
        //window.statusBarColor = Color.WHITE
        // to customise the toolbar
        edit_username.setText(userss.username)
        edit_firstname.setText(userss.fullname)
        edit_phoneno.setText(userss.phoneNumber)
        update_email.setText(userss.email)
        edit_github_acc.setText(userss.github)
        Log.d("Ochinda?","chudu"+userss.username)

    }


    override fun onBackPressed() {

        startActivity(Intent(this@EditProfile,MainActivity::class.java))
        finish()
    }
}