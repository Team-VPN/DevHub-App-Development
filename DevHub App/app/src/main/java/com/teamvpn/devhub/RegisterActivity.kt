package com.teamvpn.devhub

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var myref:DatabaseReference
    lateinit var progressDialog:ProgressDialog
    lateinit var vibrator: Vibrator
    lateinit var mskillsbutton : Button
    lateinit var auth:FirebaseAuth
    private var choosen_image_uri: URI? = null
    private var mStorageRef: StorageReference? = null
    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    lateinit var myRef:DatabaseReference
    val skills = arrayOf("App development","IoT","Machine learning","Artificial Intelligence","Python", "Java", "Kotlin","c", "C++", "c#","JavaScript","Data mining", "Cloud","Firebase","Blockchain","GO","Solidity","Ethical hacking","Embedded systems","Web development","DBMS","Cyber security","VLSI","Analog communication","Signal processing")
    val boolarray = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false)
    var skillsSelected : MutableList<String> =  mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setSupportActionBar(toolbar)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        mStorageRef = FirebaseStorage.getInstance().getReference()
        // THIS IS FOR SIGN UP BUTTON
        signup_button.setOnClickListener {
            if(!username_entry.text.isNullOrBlank()){
                val username = username_entry.text.toString()
                if(!firstname_entry.text.isNullOrBlank() and !lastname_entry.text.isNullOrBlank()){
                    val fullname = firstname_entry.text.toString() + " " + lastname_entry.text.toString()
                    if(phoneno_entry.text.toString().length == 10){
                        val phoneNumber = phoneno_entry.text.toString()
                        if(textview_date!!.text != ""){
                            val dob = textview_date!!.text.toString()
                            if(!email_entry.text.isNullOrBlank()){
                                val email = email_entry.text.toString()
                                if(password_entry.text.toString().length > 8){
                                    val password = password_entry.text.toString()
                                    val confirmpassword = confirm_password_entry.text.toString()
                                    if(password == confirmpassword){
                                        // time to upload the data to the database
                                        // first create a authentication for the user
                                        // if authentication is successful only then go with adding the user to the database
                                        // else say that sign up has failed
                                        // the below function is used for the sign up process
                                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{ task ->
                                            if(task.isSuccessful){
                                                Toasty.success(this@RegisterActivity, "Successfully Registered", Toast.LENGTH_LONG).show()
                                                val sex = "male"
                                                val userInfo = NewUserInfo(auth.uid.toString(),username,fullname,sex,dob,phoneNumber,email,skillsSelected)
                                                /////////////////////////////////////////////////////////////////
                                                //choosen_image_uri?.let { it1 -> uploadImage(it1) }
                                                database.push().setValue(userInfo)
                                                    .addOnSuccessListener {
                                                        // write was successful
                                                        Log.d("DEBUG","database created")
                                                        Toasty.success(this@RegisterActivity,"account created successfully for $fullname!",Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this, LoginActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    .addOnFailureListener{
                                                        // write was failure
                                                        Log.d("DEBUG","database creation failed")
                                                        Toasty.error(this@RegisterActivity,"failed to create account, try again in some time!",Toast.LENGTH_SHORT).show()
                                                    }
                                                ///////////////////////////////////////////////////////////////////

                                            }else {
                                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                                            }
                                        })
                                        // Toasty.success(this@RegisterActivity,"signup is successful",Toast.LENGTH_LONG).show()
                                    }else{
                                        Toasty.error(this@RegisterActivity,"Password and confirm password are not matching!, Recheck the password",Toast.LENGTH_LONG).show()
                                    }

                                }else{
                                    Toasty.warning(this@RegisterActivity,"passphrase should be > 8",Toast.LENGTH_LONG).show()
                                }
                            }else{
                                Toasty.warning(this@RegisterActivity,"we need your email to send backup settings",Toast.LENGTH_LONG).show()
                            }
                        }else{
                            Toasty.warning(this@RegisterActivity,"We need your birth date, to wish you",Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toasty.warning(this@RegisterActivity,"A phone number should be exact 10 digits",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toasty.warning(this@RegisterActivity,"fill all the fields",Toast.LENGTH_LONG).show()
                }
            }else{
                Toasty.warning(this@RegisterActivity,"fill all the fields",Toast.LENGTH_LONG).show()
            }
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

        // get the references from layout file
        textview_date = this.age_entry
        button_date = this.button_date_1

        textview_date!!.text = ""

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@RegisterActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
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
            choosen_image_uri = URI(data?.data.toString())
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

    private fun uploadImage(image_uri: URI){

        var file = Uri.fromFile(File(image_uri))
        val riversRef = mStorageRef?.child("user_profile_pictures/${file.lastPathSegment}")
        val uploadTask = riversRef?.putFile(file)
        // Register observers to listen for when the download is done or if it fails
        uploadTask?.addOnFailureListener {
            // Handle unsuccessful uploads
            Toasty.success(this@RegisterActivity,"Image uploaded succesfully",Toast.LENGTH_SHORT).show()
        }?.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Toasty.error(this@RegisterActivity,"Failed to upload image",Toast.LENGTH_SHORT).show()
        }

    }

}
// This is the dataclass for new user info
data class NewUserInfo(
    var uid:String, var username: String, var fullname: String, var sex:String, var dob: String, var phoneNumber: String, var email: String,
    var skills:MutableList<String>)