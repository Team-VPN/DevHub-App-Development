package com.teamvpn.devhub

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.MainActivity.Companion.auth
import com.teamvpn.devhub.MainActivity.Companion.database
import com.teamvpn.devhub.ModelClass.MyUserClass
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.ModelClass.userss
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

class EditProfile : AppCompatActivity() {

    lateinit var user: FirebaseUser
    lateinit var auth : FirebaseAuth
    //Creating member variables
    var refUsers: DatabaseReference? = null
    //var refUsersMain: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null
    var userId:String?=null
    lateinit var edit__username: EditText
    lateinit var edit__email: Button
    lateinit var edit__phoneno:EditText
    lateinit var edit__fullname:EditText
    lateinit var edit__github:EditText
    lateinit var edit__photo:CircleImageView
    lateinit var edit__skills : Spinner
    lateinit var skills_update : MutableList<String>
    var email : String? = null
    var skills_present : MutableList<String>? = null
    lateinit var present_boolarray : BooleanArray
    //lateinit var profileImage: CircleImageView

    val skills = arrayOf("App development","IoT","Machine learning","Artificial Intelligence","Python", "Java", "Kotlin","c", "C++", "c#","JavaScript","Data mining", "Cloud","Firebase","Blockchain","GO","Solidity","Ethical hacking","Embedded systems","Web development","DBMS","Cyber security","VLSI","Analog communication","Signal processing")
    val boolarray = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false)
    var skillsSelected : MutableList<String> =  mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar)
        //window.statusBarColor = Color.WHITE
        // to customise the toolbar
        auth = getInstance()

        edit__username = findViewById<EditText>(R.id.edit_username)
        edit__email = findViewById<Button>(R.id.update_email)
        edit__phoneno = findViewById<EditText>(R.id.edit_phoneno)
        edit__fullname = findViewById(R.id.edit_firstname)
        edit__github = findViewById(R.id.edit_github_acc)
        //edit__skills = findViewById<Spinner>(R.id.edit_skills)
        edit__photo = findViewById(R.id.edit_image)
        //profileImage = findViewById<CircleImageView>(R.id.profile_image)

        firebaseUser = getInstance().currentUser!!
        //refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        var refUsersM =
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)


        refUsersM!!.addValueEventListener(object : ValueEventListener{
            //Log.d("This2", "Is working")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    //val user: MyUserClass? = p0.getValue(MyUserClass::class.java)
                    val user: MyUserClass? = p0.getValue(MyUserClass::class.java)
                    //val user_: Users? = p0.getValue(Users::class.java)
                    //val c = user!!.getFullName()
                    //Log.d("DEBUGGING","$c")

                    Log.d("This", "Is working"+user!!.getUserName())
                    //Niran here, just added the image db from chats to the profile section. Once everything is completed, we can merge chat db with profile db.

                    email=user.getEmail()
                    edit__username.setText(user!!.getUserName())
                    edit__email.setText(user.getEmail())
                    edit__phoneno.setText(user.getPhoneNumber())
                    edit__github.setText(user.getgithub())
                    edit__fullname.setText(user.getFullName())
                    skills_present = user.getSkills()
                    val imageUrl = p0.child("image_url").getValue().toString()
                    if(imageUrl!=""){
                        Picasso.get().load(imageUrl).placeholder(R.drawable.vector_asset_profile_icon_24).into(edit_image)
                    }
                    //Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(edit_image)
                    //Picasso.get().load(user?.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(edit__photo)

                    //Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(profileImage)
                    //Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.vector_asset_profile_icon_24).into(profileImage)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUGGING","Data cancelled in profile fragment")
            }
        })

        edit_skills.setOnClickListener {
            val mskillbuilder = AlertDialog.Builder(this@EditProfile)
            mskillbuilder.setTitle("Update your skills")
            mskillbuilder.setCancelable(false)
            mskillbuilder.setMultiChoiceItems(skills, boolarray) { dialog, which, _ ->
                skillsSelected.add(skills_present.toString())
                when (which) {
                    which -> {
                        skillsSelected.add(skills[which])
                    }
                }

            }
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
            val email =email.toString()
            Log.d("Email","passed"+email)

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

        delete_account.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this@EditProfile)
            // set message of alert dialog
            dialogBuilder.setTitle("DELETE ACCOUNT")
            dialogBuilder.setMessage("Are you sure you want to delete the account!")
                // if the dialog is cancelable
                dialogBuilder.setCancelable(false)
                dialogBuilder.setPositiveButton("Yes, I'm Sure", DialogInterface.OnClickListener { dialog, id ->

                    //*************************************************************refUsersM.child(FirebaseAuth.getInstance().currentUser!!.uid).remove().addOnSuccessListener {
                        //FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener{
                        //}
                    //*************************************************************}

                    refUsersM.removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("Delete_account", "User account deleted.")
                                startActivity(Intent(this@EditProfile,LoginActivity::class.java))
                                finish()
                            }else{
                                Log.d("Delete","unsucessfull")
                            }
                        }
                })
            dialogBuilder.setNeutralButton("Cancel",DialogInterface.OnClickListener{_,_->
                Toasty.normal(this@EditProfile,"Cancelled",Toasty.LENGTH_LONG).show()

            })

            val alertdialog = dialogBuilder.create()
            alertdialog.show()

        }

    }


    override fun onBackPressed() {

        startActivity(Intent(this@EditProfile,MainActivity::class.java))
        finish()
    }
}