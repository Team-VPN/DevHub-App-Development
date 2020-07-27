package com.teamvpn.devhub

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Insets.add
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.MainActivity.Companion.database
import com.teamvpn.devhub.ModelClass.MyUserClass
import com.teamvpn.devhub.ModelClass.userss
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_edit_profile.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.jar.Manifest
import java.util.zip.Inflater

class EditProfile : AppCompatActivity() {

    lateinit var user: FirebaseUser
    lateinit var auth : FirebaseAuth
    //Creating member variables
    var refUsers: DatabaseReference? = null
    var refUsersMain: DatabaseReference? = null

    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    var user_Id: String? = null

    private val RequestCode = 438
    var imageuri: Uri? = null
    private var StorageRef: StorageReference? = null

    var firebaseUser: FirebaseUser?= null
    var userId:String?=null
    lateinit var username: EditText
    lateinit var email: Button
    lateinit var phoneno:EditText
    lateinit var fullname:EditText
    lateinit var github:EditText
    lateinit var photo:CircleImageView
    lateinit var skills : Spinner
    var for_var_skills1 : String? = null
    var for_var_skills2 : String? = null
    lateinit var skills_update : MutableList<String>
    var emaill : String? = null
    var skills_present : MutableList<String>? = null
    lateinit var present_boolarray : BooleanArray
    val skillls = arrayOf("App development","IoT","Machine learning","Artificial Intelligence","Python", "Java", "Kotlin","c", "C++", "c#","JavaScript","Data mining", "Cloud","Firebase","Blockchain","GO","Solidity","Ethical hacking","Embedded systems","Web development","DBMS","Cyber security","VLSI","Analog communication","Signal processing")
    var skillsSelected : MutableList<String> =  mutableListOf<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar)
        //window.statusBarColor = Color.WHITE
        // to customise the toolbar
        auth = getInstance()

        username = findViewById<EditText>(R.id.edit_username)
        email = findViewById<Button>(R.id.update_email)
        phoneno = findViewById<EditText>(R.id.edit_phoneno)
        fullname = findViewById(R.id.edit_firstname)
        github = findViewById(R.id.edit_github_acc)
        //edit__skills = findViewById<Spinner>(R.id.edit_skills)
        photo = findViewById(R.id.edit_image)
        //profileImage = findViewById<CircleImageView>(R.id.profile_image)

        mFirebaseInstance = FirebaseDatabase.getInstance()
        val user_ = FirebaseAuth.getInstance().getCurrentUser()
        user_Id = user_?.getUid()

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance!!.getReference("users")
        firebaseUser = getInstance().currentUser!!
        StorageRef = FirebaseStorage.getInstance().reference.child("user_profile_pictures")


        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        refUsersMain = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)
        var refUsersM =
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)


        refUsersM!!.addValueEventListener(object : ValueEventListener {
            //Log.d("This2", "Is working")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    //val user: MyUserClass? = p0.getValue(MyUserClass::class.java)
                    //val user: MyUserClass? = p0.getValue(MyUserClass::class.java)
                    //val user_: Users? = p0.getValue(Users::class.java)
                    //val c = user!!.getFullName()
                    //Log.d("DEBUGGING","$c")

                    //Log.d("This", "Is working" + user!!.getUserName())
                    emaill = p0.child("email").value.toString()
                    username.setText(p0.child("username").value.toString())
                    email.setText(p0.child("email").value.toString())
                    phoneno.setText(p0.child("phoneNumber").value.toString())
                    github.setText(p0.child("github").value.toString())
                    fullname.setText(p0.child("fullname").value.toString())
                    skills_present = p0.child("skills").value as MutableList<String>?
                    Log.d("Skills check","check"+skills_present)
                    val imageUrl = p0.child("image_url").getValue().toString()
                    if (imageUrl != "") {
                        Picasso.get().load(imageUrl)
                            .placeholder(R.drawable.vector_asset_profile_icon_24).into(edit_image)
                    }
                    //Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(edit_image)
                    //Picasso.get().load(user?.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(edit__photo)

                    //Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(profileImage)
                    //Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(profileImage)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUGGING", "Data cancelled in profile fragment")
            }
        })




        edit_skills.setOnClickListener {
            val boolarray = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false)
            for (for_var_skills1 in this!!.skills_present!!){
                var x =-1
                for(for_var_skills2 in skillls){
                    x += 1
                    if(for_var_skills1 == for_var_skills2){
                        boolarray[x] = true
                    }
                }

            }

            Log.d("For loop check","Boolean array"+boolarray)
            val mskillbuilder = AlertDialog.Builder(this@EditProfile)
            mskillbuilder.setTitle("Update your skills")
            mskillbuilder.setCancelable(true)
            mskillbuilder.setMultiChoiceItems(skillls, boolarray) { dialog, which, _ ->
                skillsSelected = skills_present as MutableList<String>
                when (which) {
                    which -> {
                        skillsSelected.add(skillls[which])
                    }
                }

            }
            //Log.d("Skills list","Check"+skillsSelected)
            mskillbuilder.setPositiveButton("add these skills") { _, _ ->
                Toasty.normal(
                    this@EditProfile,
                    "Updated",
                    Toast.LENGTH_SHORT
                ).show()
            }

            mskillbuilder.setNegativeButton("cancel") { _, _ ->

            }

            val mskilldialog = mskillbuilder.create()
            mskilldialog.show()
        }

        password_edit.setOnClickListener {
            // build alert dialog
            val email = email.toString()
            Log.d("Email", "passed" + email)

            val dialogBuilder = android.app.AlertDialog.Builder(this)
            // set message of alert dialog
            dialogBuilder.setMessage("Click on 'Continue' to Reset the Password")
                // if the dialog is cancelable
                .setCancelable(false)
                .setPositiveButton("Continue", DialogInterface.OnClickListener { dialog, id ->

                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toasty.success(
                                    this@EditProfile,
                                    "Reset link sent to your email",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else {
                                Toasty.info(
                                    this@EditProfile,
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
        update_image.setOnClickListener {
            pickImage()

        }

        delete_account.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this@EditProfile)
            // set message of alert dialog
            dialogBuilder.setTitle("DELETE ACCOUNT")
            dialogBuilder.setMessage("Are you sure you want to delete the account!")
            // if the dialog is cancelable
            dialogBuilder.setCancelable(false)
            dialogBuilder.setPositiveButton(
                "Yes, I'm Sure",
                DialogInterface.OnClickListener { dialog, id ->

                    //*************************************************************refUsersM.child(FirebaseAuth.getInstance().currentUser!!.uid).remove().addOnSuccessListener {
                    //FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener{
                    //}
                    //*************************************************************}

                    refUsersM.removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("Delete_account", "User account deleted.")
                                startActivity(Intent(this@EditProfile, LoginActivity::class.java))
                                finish()
                            } else {
                                Log.d("Delete", "unsucessfull")
                            }
                        }
                })
            dialogBuilder.setNeutralButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                Toasty.normal(this@EditProfile, "Cancelled", Toasty.LENGTH_LONG).show()

            })

            val alertdialog = dialogBuilder.create()
            alertdialog.show()

        }
        update_profile.setOnClickListener {

                       if (edit_phoneno.text.toString().length == 10){
                           if(!edit_firstname.text.isNullOrBlank()){
                               if (!edit_username.text.isNullOrBlank()) {
                                   if (!edit_github_acc.text.isNullOrBlank()) {

                                       update(
                                           refUsersM.toString(),
                                           edit_username.text.toString(),
                                           edit_firstname.text.toString(),
                                           edit_phoneno.text.toString(),
                                           update_email.text.toString(),
                                           edit_github_acc.text.toString(),
                                           skillsSelected,
                                           edit_image.toString()
                                       )


                                       Toasty.info(this@EditProfile, "Updated", Toast.LENGTH_LONG)
                                           .show()
                                       //Log.d("Updation check", "Username Updated")
                                   }else{
                                       Toasty.warning(this,"Github account not specified",Toast.LENGTH_SHORT).show()
                                   }
                               }else{
                                   Toasty.warning(this,"Username cannot be blank",Toast.LENGTH_SHORT).show()
                               }
                           }else{
                               Toasty.warning(this,"Name cannot be blank",Toast.LENGTH_SHORT).show()
                           }

                        }else{
                           Toasty.warning(this,"Phone number should be of 10 digits",Toast.LENGTH_SHORT).show()
                       }




        }
        update_email.setOnClickListener {
            val inflater = layoutInflater
            val inflate_view = inflater.inflate(R.layout.update_email_custom_view,null)
            val emailEdt = inflate_view.findViewById(R.id.custom_email_update) as EditText
            val alertDialog=AlertDialog.Builder(this)
            alertDialog.setTitle("Update Email :")
            alertDialog.setView(inflate_view)
            alertDialog.setCancelable(true)
            alertDialog.setNegativeButton("Cancel"){dialog, which ->
                Toasty.info(this,"Cancelled",Toast.LENGTH_SHORT).show()
            }
            alertDialog.setPositiveButton("Update"){dialog, which ->
                val emil = emailEdt.text.toString()
                update_email.setText(emil)
                Toasty.info(this,"Email updated",Toast.LENGTH_SHORT).show()
            }
            val dialog = alertDialog.create()
            dialog.show()
        }

    }
    private fun update(uid:String?, username: String?, fullname: String?,phoneno: String?,email:String?,github:String?,skillsu : MutableList<String>,imageuri:String?) {
        // Create new post at /user-posts/$userid/$postid and at
        val firebaseuid= FirebaseAuth.getInstance().currentUser!!.uid
        val database=FirebaseDatabase.getInstance().getReference("users/$firebaseuid")
        val child_Updates=HashMap<String,Any>()
        child_Updates["username"]=username.toString()
        child_Updates["fullname"]=fullname.toString()
        child_Updates["phoneNumber"]=phoneno.toString()
        child_Updates["email"]=email.toString()
        child_Updates["skills"]= skillsu.toMutableList()
        child_Updates["github"]=github.toString()
        database.updateChildren(child_Updates)


    }
    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null)
        {
            imageuri = data.data
            uploadImageToDatabase()

        }
    }

    private fun uploadImageToDatabase()
    {
        val progressBar = ProgressDialog(this)
        progressBar.setMessage("Image is uploading, please wait!")
        progressBar.show()

        if (edit_image!=null)
        {

            val fileRef = StorageRef!!.child(firebaseUser!!.uid.toString())

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageuri!!)


            uploadTask.continueWithTask(Continuation < UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let{
                        throw it

                    }
                }
                return@Continuation fileRef.downloadUrl
            } ).addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val mUri = downloadUrl.toString()


                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = mUri
                    refUsersMain!!.updateChildren(mapProfileImg)
                        mapProfileImg["image_url"] = mUri
                    refUsers!!.updateChildren(mapProfileImg)




                    progressBar.dismiss()
                }
            }
        }

    }
    override fun onBackPressed() {

        startActivity(Intent(this@EditProfile,MainActivity::class.java))
        finish()
    }
}