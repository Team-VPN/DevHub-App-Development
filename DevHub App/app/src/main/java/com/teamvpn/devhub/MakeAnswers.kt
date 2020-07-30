package com.teamvpn.devhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_make_answers.*
import kotlinx.android.synthetic.main.activity_view_posts.*

class MakeAnswers : AppCompatActivity() {
    companion object{
        lateinit var companionChild:String
        lateinit var companionUID:String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_answers)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val keyChild = intent.getStringExtra("dateTime")
        val uid_of_the_qn_seeker = intent.getStringExtra("GetUserID")
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUserss = FirebaseDatabase.getInstance().reference

        refUserss.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(uid_of_the_qn_seeker!=null){
                    val current_post = p0.child("posts/${uid_of_the_qn_seeker.toString()}/$keyChild")
                    companionUID = uid_of_the_qn_seeker
                    companionChild = keyChild
                    textView016.text = current_post.child("question_in_single_line").value.toString()
                    textView017.text = current_post.child("question_in_brief").value.toString()
                    if(current_post.hasChild("image_uri")){
                        val url = current_post.child("image_uri").value.toString()
                        Picasso.get().load(url).into(imageView05)
                        imageView05.visibility = View.VISIBLE
                    }else{
                        imageView05.visibility = View.GONE
                    }
                }


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        button07.setOnClickListener {
            val intent = Intent(this@MakeAnswers,SayAnswer::class.java)
            intent.putExtra("postUserUID",uid_of_the_qn_seeker.toString())
            intent.putExtra("dateTime",keyChild.toString())
            startActivity(intent)
        }

    }
}