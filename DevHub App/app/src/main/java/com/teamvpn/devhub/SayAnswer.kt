package com.teamvpn.devhub

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.MakeAnswers.Companion.companionChild
import com.teamvpn.devhub.MakeAnswers.Companion.companionUID
import com.teamvpn.devhub.Notifications.*
import com.teamvpn.devhub.mfragment.APIService
import com.teamvpn.devhub.ui.post_the_question_with_img
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_say_answer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class SayAnswer : AppCompatActivity() {
    lateinit var apiService:APIService
    lateinit var image_uri: Uri
    private val image_pick_code = 1000
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888
    lateinit var global_bitmap:Bitmap
    private val permission_code = 1001
    lateinit var question_in_a_line: EditText
    lateinit var question_in_brief: EditText
    lateinit var select_documents: Button
    lateinit var select_skills: Button
    lateinit var post_question: Button
    lateinit var progressDialog: ProgressDialog
    var image_selected = false
    lateinit var vibrator: Vibrator
    lateinit var user_name:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_say_answer)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val dateTime = companionChild
        val uid_of_the_qn_seeker = companionUID

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        updateToken(FirebaseInstanceId.getInstance().token)

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        val refUsers = FirebaseDatabase.getInstance().reference

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user_name = p0.child("users/$firebaseUserID/username").value.toString()

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUGGING", "Data cancelled in SayAnswer Activity")
            }
        })

        button8.setOnClickListener {
            //check runtime permission
            vibrator.vibrate(60)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(
                intent,
                CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        button6.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Thank you for your answer, we are working on saving the data")
            progressDialog.setCanceledOnTouchOutside(false)
            if(!editTextTextMultiLine2.text.isNullOrBlank()){
                val answer = editTextTextMultiLine2.text.toString()
                if(uid_of_the_qn_seeker!= null && dateTime != null) {
                    vibrator.vibrate(60)
                    if(image_selected){
                        progressDialog.show()
                        uploadImageAlongWithData(firebaseUserID.toString(),answer,uid_of_the_qn_seeker,dateTime,global_bitmap)
                        image_selected = false
                    }else{
                        progressDialog.show()
                        sendDataWithoutImage(firebaseUserID.toString(),answer,uid_of_the_qn_seeker,dateTime)
                        image_selected = false
                    }
                }

            }else{
                Toasty.warning(this@SayAnswer,"You haven't written any answer, what is the point of submitting it", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                imageView6.setImageBitmap(bitmap)
                image_selected = true
                imageView6.visibility = View.VISIBLE
            }
        }
    }

    private fun sendDataWithoutImage(uid:String,answer:String,uidOfUser:String,dateTime:String){
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var hashMap = HashMap<String,String>()
        hashMap["uid"] = uid
        hashMap["answer"] = answer
        progressDialog.setMessage("Data is uploading....")
        val db = FirebaseDatabase.getInstance().reference.child("posts/$uidOfUser/$dateTime/answers/$firebaseUserID")
        db.updateChildren(hashMap as Map<String, Any>)
            .addOnSuccessListener {
                sendNotification(uidOfUser,user_name)
                progressDialog.setMessage("Answer is saved successfully...")
                progressDialog.dismiss()
                Toasty.success(this,"Answer is saved successfully...",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SayAnswer,MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener{
                progressDialog.setMessage("Failed to upload answer...")
                progressDialog.dismiss()
                Toasty.error(this,"Failed to upload answer...",Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImageAlongWithData(uid: String, answer:String, uidOfUser:String, dateTime:String, global_bitmap:Bitmap){
        val baos = ByteArrayOutputStream()
        var hashMap = HashMap<String,String>()
        hashMap["uid"] = uid
        hashMap["answer"] = answer
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        global_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val current = LocalDateTime.now().toString().slice(0..18)
        val mStorageRef = FirebaseStorage.getInstance().getReference().child("images_in_posts")
        val db = FirebaseDatabase.getInstance().reference.child("posts/$uidOfUser/$dateTime/answers/$firebaseUserID")
        val uploadTask: UploadTask = mStorageRef!!.child(firebaseUserID).child(current).putBytes(data)
        val task = uploadTask.continueWithTask {
                task->
            val downloadUrl = task.result//.substring(0,task.result.toString().indexOf("&token"))
            // THIS IS HOW I AM GETTING URL TO DOWNLOAD IMAGE
            val uri: Task<Uri> = downloadUrl.storage.downloadUrl
            while (!uri.isComplete);
            val url: Uri = uri.result
            Log.d("IAMCHECKING","$url and $current")
            hashMap["image_url"] = url.toString()
            db.updateChildren(hashMap as Map<String, Any>)
                .addOnSuccessListener {
                    // write was successful
                    Log.d("IAMCHECKING","I AM HERE")
                    sendNotification(uidOfUser,user_name)
                    Toasty.success(this,"Good, you have contributed!",Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    startActivity(Intent(this@SayAnswer,MainActivity::class.java))
                    finishAffinity()
                }
                .addOnFailureListener{
                    Log.d("IAMCHECKING","Nanillidini")
                    // write was failure
                     Toasty.error(this,"failed to save the answers",Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    image_selected = true
                }

            if(!task.isSuccessful){
                Log.d("IAMCHECKING","naa inga iruken")
                Toasty.error(this,"failed to make post",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                image_selected = true
            }
            mStorageRef!!.downloadUrl
        }.addOnCompleteListener {
                task ->
            Log.d("IAMCHECKING","nen ikkada unnanu")
            if(task.isSuccessful){
                Log.d("IAMCHECKING","aham asmi")
                startActivity(Intent(this@SayAnswer,MainActivity::class.java))
                finishAffinity()
                progressDialog.dismiss()
                image_selected = false
            }
        }

        }


    private fun sendNotification(receiverId: String?, userName: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)

                    val data = Data(
                        firebaseUser,
                        R.mipmap.ic_launcher,
                        "$userName has answered your question. Open the app to view the answer. Answer will be available at your profile section",
                        "Hey!, your question is answered",
                        companionUID
                    )

                    val sender = Sender(data!!, token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object: Callback<MyResponse>
                        {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            )
                            {
                                if(response.code() == 200)
                                {
                                    if(response.body()!!.success !== 1)
                                    {
                                        Toasty.error(this@SayAnswer, "Failed, Nothing happened", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {


                            }


                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })


    }

    private fun updateToken(token: String?) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(token1)
    }


}