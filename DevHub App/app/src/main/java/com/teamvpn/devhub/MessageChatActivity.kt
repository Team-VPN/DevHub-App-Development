package com.teamvpn.devhub

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.AdapterClasses.ChatAdapter
import com.teamvpn.devhub.ModelClass.Chat
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.Notifications.*
import com.teamvpn.devhub.mfragment.APIService
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? =  null
    var chatAdapter : ChatAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view_chat: RecyclerView
    var reference: DatabaseReference? = null

    var notify = false
    var apiService : APIService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@MessageChatActivity, MainChat::class.java)//could be unstable
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)


        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_chat = findViewById(R.id.recycler_view_chat)
        recycler_view_chat.setHasFixedSize(true)
        var linearLayoutmanager = LinearLayoutManager(applicationContext)
        linearLayoutmanager.stackFromEnd = true
        recycler_view_chat.layoutManager = linearLayoutmanager

        reference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(userIdVisit)
        reference!!.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(p0: DataSnapshot) {

                val user: Users? = p0.getValue(Users::class.java)
                username_mchat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mc)


                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }

            override fun onCancelled(error: DatabaseError) {



            }



        })

        send_message_btn.setOnClickListener {
            notify = true
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
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick an Image"), 438)

        }

        seenMessage(userIdVisit)

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

                    //later = Push notifications
                }
            }
        val usersReference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)

        usersReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                val user = p0.getValue(Users::class.java)
                if(notify)
                {
                    sendNotification(receiverId, user!!.getUserName(), message)
                }
                notify = false


            }

            override fun onCancelled(error: DatabaseError) {



            }
        })


        }

    private fun sendNotification(receiverId: String?, userName: String?, message: String)
    {

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
                        firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$userName : $message",
                        "New Message!",
                        userIdVisit
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
                                if(response.code() == 200) {
                                    if(response.body()!!.success !== 1) {
                                        Toasty.error(this@MessageChatActivity, "Failed, Nothing happened", Toast.LENGTH_SHORT).show()
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
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful)
                            {
                                //use fcm

                                val reference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)

                                reference.addValueEventListener(object: ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot)
                                    {
                                        val user = p0.getValue(Users::class.java)
                                        if(notify)
                                        {
                                            sendNotification(userIdVisit, user!!.getUserName(), "sent you an image.")
                                        }
                                        notify = false


                                    }

                                    override fun onCancelled(error: DatabaseError) {



                                    }
                                })
                            }

                        }

                    progressBar.dismiss()


                }
            }
        }
    }
    private fun retrieveMessages(senderId: String, receiverId: String?, receiverimageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals((senderId)))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatAdapter = ChatAdapter(this@MessageChatActivity, (mChatList as ArrayList<Chat>), receiverimageUrl!!)
                    recycler_view_chat.adapter = chatAdapter

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })

    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && chat.getSender().equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)

                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {



            }
        })

    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)

    }

}