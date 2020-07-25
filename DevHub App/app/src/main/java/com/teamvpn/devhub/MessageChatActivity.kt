package com.teamvpn.devhub

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.Users
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(userIdVisit)
        reference.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(p0: DataSnapshot) {
                T
                val user: Users? = p0.getValue(Users::class.java)
                username_mchat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mc)
            }

            override fun onCancelled(error: DatabaseError) {



            }



        })

        send_message_btn.setOnClickListener {
            val message = text_message.text.toString()
            if (message == "")
                {
                    Toast.makeText(this@MessageChatActivity, "Please write a message first", Toast.LENGTH_SHORT).show()
                }
                else
            {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)

            }
            text_message.setText("")

        }

        attach_image_file_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick an Image"), 438)



        }


    }

    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String)
    {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messagehashMap = HashMap<String, Any?>()

        messagehashMap["sender"] = senderId
        messagehashMap["message"] = message
        messagehashMap["receiver"] = receiverId
        messagehashMap["isseen"] = false
        messagehashMap["url"] = ""
        messagehashMap["messageId"] = messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messagehashMap)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful)
                {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{

                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists())
                            {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }

                            val chatsListReceiverReference = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)

                            chatsListReceiverReference.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })



                    //use fcm

                    val reference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)

                    //later = Push notifications
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 438 && resultCode == RESULT_OK && data!=null &&data!!.data!=null)
        {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait!")
            progressBar.show()

            val fileUri = data.data
            val storagereference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storagereference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)


            uploadTask.continueWithTask(Continuation < UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let{
                        throw it

                    }
                }
                return@Continuation filePath.downloadUrl
            } ).addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val mUri = downloadUrl.toString()

                    val messagehashMap = HashMap<String, Any?>()

                    messagehashMap["sender"] = firebaseUser!!.uid
                    messagehashMap["message"] = "sent you an image."//don't change anything
                    messagehashMap["receiver"] = userIdVisit
                    messagehashMap["isseen"] = false
                    messagehashMap["url"] = mUri
                    messagehashMap["messageId"] = messageId


                    ref.child("Chats").child(messageId!!).setValue(messagehashMap)

                    progressBar.dismiss()


                }
            }




        }
    }

}