package com.teamvpn.devhub

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var mskillsbutton : Button
    val skills = arrayOf("App development","IoT","Machine learning","Artificial Intelligence","Python", "Java", "Kotlin","c", "C++", "c#","JavaScript","Data mining", "Cloud","Firebase","Blockchain","GO","Solidity","Ethical hacking","Embedded systems","Web development","DBMS","Cyber security","VLSI","Analog communication","Signal processing")
    val boolarray = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        supportActionBar?.hide()

        mskillsbutton = findViewById(R.id.country_entry)
        mskillsbutton.setOnClickListener {
            val mskillbuilder = AlertDialog.Builder(this@RegisterActivity)
            mskillbuilder.setTitle("Select your skills")
            mskillbuilder.setCancelable(false)
            mskillbuilder.setMultiChoiceItems(skills,boolarray){dialog, which, _->
                when(which){
                    which ->{
                        Toast.makeText(this@RegisterActivity,skills[which],Toast.LENGTH_LONG).show()
                    }
                }

            }
            mskillbuilder.setPositiveButton("Save"){_,_->
                Toast.makeText(this@RegisterActivity,"Saved", Toast.LENGTH_LONG).show()
            }
            mskillbuilder.setNegativeButton("Don't Save"){_,_->
                Toast.makeText(this@RegisterActivity,"Not Saved", Toast.LENGTH_LONG).show()
            }
            mskillbuilder.setNeutralButton("Cancel"){_,_->
                finish()
            }
            val mskilldialog = mskillbuilder.create()
            mskilldialog.show()
        }

        login_button_redirect.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        image_upload_button.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, permission_code)
                } else {
                    //permission granted
                    pickinagrfromgallery()
                }
            } else {

            }


        }
    }
    private fun pickinagrfromgallery(){
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
                    pickinagrfromgallery()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == image_pick_code){
            user_image.setImageURI(data?.data)
        }
    }

    }
