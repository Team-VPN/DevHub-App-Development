package com.teamvpn.devhub.mfragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.storage.StorageManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.R
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_setting.view.*


/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment()
{
    var UserReference : DatabaseReference? = null
    var UserReferenceM : DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var StorageRef: StorageReference? = null
    private var coverChecker : String? = null
    private var socialChecker : String? = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        UserReference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)
        UserReferenceM = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        StorageRef = FirebaseStorage.getInstance().reference.child("user_profile_pictures")//check

        UserReference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)

                    if(context!=null)
                    {
                        view.username_settings.text= user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user.getCover()).into(view.cover_image_settings)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {



            }

        })

        view.profile_image_settings.setOnClickListener{
            pickImage()

        }
        view.cover_image_settings.setOnClickListener{
            coverChecker = "cover"
            pickImage()

        }
        view.set_link.setOnClickListener{
            socialChecker = "linkedin"
            setSocialLinks()
        }
        view.set_git.setOnClickListener{
            socialChecker = "github"
            setSocialLinks()
        }
        view.set_stack.setOnClickListener{
            socialChecker = "stackof"
            setSocialLinks()
        }




        return view
    }

    private fun setSocialLinks() {

        val builder: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if(socialChecker == "github")
        {
            builder.setTitle("Write Username:")
        }
        else
        {
            builder.setTitle("Write URL:")
        }

        val editText = EditText(context)

        if(socialChecker == "github")
        {
            editText.hint = ("Eg: Felirox")
        }
        else
        {
            editText.hint = ("Eg: www.website.com/...")
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
            dialog, which ->
            var str = editText.text.toString()

            if(str == "")
            {
                Toast.makeText(context, "Hey! Write something.", Toast.LENGTH_LONG).show()
            }
            else
            {
                saveSocialLink(str)
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {

        val mapSocial = HashMap<String, Any>()
     //   mapSocial = "cover"] = mUri

        //overChecker = ""

        when(socialChecker)
        {
            "linkedin" ->
            {
                mapSocial["linkedin"] = "https://$str"
            }
            "github" ->
            {
                mapSocial["github"] = "https://www.github.com/$str"
            }
            "stackof" ->
            {
                mapSocial["stackof"] = "https://$str"
            }
        }
        UserReference!!.updateChildren(mapSocial).addOnCompleteListener{
            task ->
            if (task.isSuccessful)
            {
                Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
            }
        }
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
            imageUri = data.data
            Toast.makeText(context, "Uploading!", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()

        }
    }

    private fun uploadImageToDatabase()
    {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait!")
        progressBar.show()

        if (imageUri!=null)
        {

            val fileRef = StorageRef!!.child(firebaseUser!!.uid.toString())

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)


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

                    if(coverChecker == "cover")
                    {
                       val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = mUri
                        UserReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else
                    {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = mUri
                        UserReference!!.updateChildren(mapProfileImg)
                        mapProfileImg["image_url"] = mUri
                        UserReferenceM!!.updateChildren(mapProfileImg)
                        coverChecker = ""



                    }
                    progressBar.dismiss()
                }
            }
        }

    }
}