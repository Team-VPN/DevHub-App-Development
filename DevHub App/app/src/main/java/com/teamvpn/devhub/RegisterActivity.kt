package com.teamvpn.devhub

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var progressDialog:ProgressDialog
    lateinit var vibrator: Vibrator
    lateinit var mskillsbutton : Button
    val skills = arrayOf("App development","IoT","Machine learning","Artificial Intelligence","Python", "Java", "Kotlin","c", "C++", "c#","JavaScript","Data mining", "Cloud","Firebase","Blockchain","GO","Solidity","Ethical hacking","Embedded systems","Web development","DBMS","Cyber security","VLSI","Analog communication","Signal processing")
    val boolarray = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false)
    var skillsSelected : MutableList<String> =  mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        supportActionBar?.hide()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // THIS IS FOR SIGN UP BUTTON
        signup_button.setOnClickListener {
            
        }

        mskillsbutton = findViewById(R.id.country_entry)
        mskillsbutton.setOnClickListener {
            val mskillbuilder = AlertDialog.Builder(this@RegisterActivity)
            mskillbuilder.setTitle("Select your skills")
            mskillbuilder.setCancelable(true)
            mskillbuilder.setMultiChoiceItems(skills,boolarray){dialog, which, _->
                when(which){
                    which ->{
                        skillsSelected.add(skills[which])
                    }
                }

            }

            mskillbuilder.setPositiveButton("add these skills"){_,_->
                Toasty.normal(this@RegisterActivity,"Excellent!, You got great skills", Toast.LENGTH_SHORT).show()
            }

            mskillbuilder.setNegativeButton("cancel"){_,_->

            }

            val mskilldialog = mskillbuilder.create()
            mskilldialog.show()
        }

        login_button_redirect.setOnClickListener {
            // Handler code here.
            vibrator.vibrate(60)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        select_image_button.setOnClickListener {
            //check runtime permission
            vibrator.vibrate(60)

            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("opening gallery")
            progressDialog.setCanceledOnTouchOutside(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied

                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, permission_code)
                } else {
                    //permission granted
                    progressDialog.show()
                    pickimagefromgallery()
                }
            }


        }
    }
    private fun pickimagefromgallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, image_pick_code)

    }
    companion object {
        private val image_pick_code = 1000

        private val permission_code = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            permission_code -> {
                if(grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    progressDialog.show()
                    pickimagefromgallery()
                }
                else{
                    Toasty.warning(this@RegisterActivity,"Permission denied!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressDialog.dismiss()
        if(resultCode == Activity.RESULT_OK && requestCode == image_pick_code){
            user_profile_image.setImageURI(data?.data)
        }
    }

    override fun onBackPressed() {
        val alertBox = AlertDialog.Builder(this@RegisterActivity)
        alertBox.setTitle("Do you wish to discard sign up process?")
        alertBox.setIcon(R.mipmap.ic_launcher)
        alertBox.setMessage("Its ok, come back any time to sign up to our service")
        alertBox.setCancelable(true)
        alertBox.setPositiveButton("yes"){_,_->
            finish()
        }
        alertBox.setNegativeButton("No"){_,_->

        }
        alertBox.create().show()
    }


}

