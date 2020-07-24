package com.teamvpn.devhub.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.teamvpn.devhub.MainActivity
import com.teamvpn.devhub.MainActivity.Companion.vibrator
import com.teamvpn.devhub.R
import com.teamvpn.devhub.post_made_successful_activity
import es.dmoral.toasty.Toasty
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*


class PostFragment : Fragment() {
    lateinit var image_uri: Uri
    private val image_pick_code = 1000
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888
    lateinit var global_bitmap:Bitmap
    private val permission_code = 1001
    lateinit var question_in_a_line:EditText
    lateinit var question_in_brief:EditText
    lateinit var image_for_preview:ImageView
    lateinit var select_documents:Button
    lateinit var select_skills:Button
    lateinit var post_question:Button
    lateinit var progressDialog: ProgressDialog
    var image_selected = false
    val skills = arrayOf("App development","IoT","Machine learning","Artificial Intelligence","Python", "Java", "Kotlin","c", "C++", "c#","JavaScript","Data mining", "Cloud","Firebase","Blockchain","GO","Solidity","Ethical hacking","Embedded systems","Web development","DBMS","Cyber security","VLSI","Analog communication","Signal processing")
    val boolarray = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false)
    var skillsSelected : MutableList<String> =  mutableListOf<String>()

    lateinit var database: DatabaseReference
    lateinit var auth:FirebaseAuth
    var mStorageRef: StorageReference? = null
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_post, container, false)

        // The below lines of code is basically controls the edit text, image preview and submit button and attach documents and select skills button
        question_in_a_line = view.findViewById<EditText>(R.id.editTextTextPersonName)
        question_in_brief = view.findViewById<EditText>(R.id.editTextTextMultiLine)
        image_for_preview = view.findViewById<ImageView>(R.id.image_preview)
        select_documents = view.findViewById(R.id.button2)
        select_skills = view.findViewById(R.id.button3)
        post_question = view.findViewById(R.id.button4)
        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("posts")
        mStorageRef = FirebaseStorage.getInstance().getReference().child("images_in_posts")
        // Control all the fields declared above
        post_question.setOnClickListener {
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Hang on for few seconds, we are working on posting your doubt")
            progressDialog.setCanceledOnTouchOutside(false)
            if(!question_in_a_line.text.isNullOrBlank()){
                val question_in_a_single_line = question_in_a_line.text.toString()
                if(!question_in_brief.text.isNullOrBlank()){
                    val question_in_multi_lines = question_in_brief.text.toString()
                    if(skillsSelected.size > 0){
                        vibrator.vibrate(60)
                        if(image_selected){
                            progressDialog.show()
                            uploadImageAlongWithData(global_bitmap,question_in_a_single_line,question_in_multi_lines,skillsSelected)
                            image_selected = false
                        }else{
                            progressDialog.show()
                            sendDataWithoutImage(question_in_a_single_line,question_in_multi_lines,skillsSelected)
                            image_selected = false
                        }
                    }else{
                        context?.let { it1 -> Toasty.warning(it1,"Select at least one skill!",Toast.LENGTH_SHORT).show() }
                    }
                }else{
                    context?.let { it1 -> Toasty.warning(it1,"Brief your question in detail",Toast.LENGTH_SHORT).show() }
                }
            }else{
                context?.let { it1 -> Toasty.warning(it1,"Frame your question in a single line",Toast.LENGTH_SHORT).show() }
            }
        }

        select_skills.setOnClickListener {
            val mskillbuilder = context?.let { it1 -> AlertDialog.Builder(it1) }
            mskillbuilder?.setTitle("Select your skills")
            mskillbuilder?.setCancelable(true)
            mskillbuilder?.setMultiChoiceItems(skills,boolarray){dialog, which, _->
                when(which){
                    which ->{
                        skillsSelected.add(skills[which])
                    }
                }
            }
            mskillbuilder?.setPositiveButton("add these skills"){_,_->
                context?.let { it1 -> Toasty.normal(it1,"Excellent!, You got great skills", Toast.LENGTH_SHORT).show() }
            }
            mskillbuilder?.setNegativeButton("cancel"){_,_->

            }
            val mskilldialog = mskillbuilder?.create()
            mskilldialog?.show()
        }


        select_documents.setOnClickListener {
            //check runtime permission
            vibrator.vibrate(60)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(
                intent,
                CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }


        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val bmp = data?.extras!!["data"] as Bitmap?
                val stream = ByteArrayOutputStream()
                bmp!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()
                // convert byte array to Bitmap
                val bitmap = BitmapFactory.decodeByteArray(
                    byteArray, 0,
                    byteArray.size
                )
                global_bitmap = bitmap
                image_for_preview.setImageBitmap(bitmap)
                image_selected = true
                image_for_preview.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImageAlongWithData(bitmap: Bitmap, question:String, question_in_brief: String, skills_selected: MutableList<String>) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val current = LocalDateTime.now().toString().slice(0..18)
        val uploadTask: UploadTask = mStorageRef!!.child(auth.uid.toString()).child(current).putBytes(data)
        val task = uploadTask.continueWithTask {
                task->
            val downloadUrl = task.result//.substring(0,task.result.toString().indexOf("&token"))
            val userInfo = auth.uid.toString()
            // THIS IS HOW I AM GETTING URL TO DOWNLOAD IMAGE
            val uri: Task<Uri> = downloadUrl.storage.downloadUrl
            while (!uri.isComplete);
            val url: Uri = uri.result
            Log.d("IAMCHECKING","$url and $current")
            val post = post_the_question_with_img(userInfo,question,question_in_brief,url.toString(),skills_selected)
            database.child(auth.uid.toString()).child(current).setValue(post)
                .addOnSuccessListener {
                    // write was successful
                    Log.d("IAMCHECKING","I AM HERE")
                    context?.let { it1 -> Toasty.success(it1,"post is successful",Toast.LENGTH_SHORT).show() }
                    progressDialog.dismiss()
                    image_for_preview.visibility = View.INVISIBLE
                    (activity as MainActivity).changeFragment()
                    startActivity(Intent(context,post_made_successful_activity::class.java))
                }
                .addOnFailureListener{
                    Log.d("IAMCHECKING","Nanillidini")
                    // write was failure
                    context?.let { it1 -> Toasty.error(it1,"failed to post",Toast.LENGTH_SHORT).show() }
                    progressDialog.dismiss()
                    image_selected = true
                }

            if(!task.isSuccessful){
                Log.d("IAMCHECKING","naa inga iruken")
                context?.let { it1 -> Toasty.error(it1,"failed to make post",Toast.LENGTH_SHORT).show() }
                progressDialog.dismiss()
                image_selected = true
            }
            mStorageRef!!.downloadUrl
        }.addOnCompleteListener {
                task ->
            Log.d("IAMCHECKING","nen ikkada unnanu")
            if(task.isSuccessful){
                Log.d("IAMCHECKING","aham asmi")
                (activity as MainActivity).changeFragment()
                startActivity(Intent(context,post_made_successful_activity::class.java))
                progressDialog.dismiss()
                image_selected = false
            }
        }
    }


    // This working perfectly
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendDataWithoutImage(question:String, question_in_brief: String, skills_selected: MutableList<String>){
        val userInfo = auth.uid.toString()
        val post = post_the_question_without_img(userInfo,question,question_in_brief,skills_selected)
        val temp = UUID.randomUUID().toString()
        val current = LocalDateTime.now().toString().slice(0..18)
            database.child(auth.uid.toString()).child(current).setValue(post)
                .addOnSuccessListener {
                    // write was successful
                    context?.let { it1 -> Toasty.success(it1,"post is successful!",Toast.LENGTH_SHORT).show() }
                    progressDialog.dismiss()
                    image_selected  = false
                    image_for_preview.visibility = View.INVISIBLE
                    (activity as MainActivity).changeFragment()
                    startActivity(Intent(context,post_made_successful_activity::class.java))
                }
                .addOnFailureListener{
                    // write was failure
                    context?.let { it1 -> Toasty.error(it1,"failed to post the question",Toast.LENGTH_SHORT).show() }
                    progressDialog.dismiss()
                    image_selected = false
                    image_for_preview.visibility = View.INVISIBLE
                }

    }


}

data class post_the_question_with_img(var uid:String,var question_in_single_line:String, var question_in_brief:String ,var image_uri:String,var skills_selected:MutableList<String>)
data class post_the_question_without_img(var uid:String,var question_in_single_line:String, var question_in_brief:String ,var skills_selected:MutableList<String>)