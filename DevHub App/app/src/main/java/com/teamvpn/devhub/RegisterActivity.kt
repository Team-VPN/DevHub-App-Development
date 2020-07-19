package com.teamvpn.devhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}