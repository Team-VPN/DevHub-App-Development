package com.teamvpn.devhub

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.MyPostClass
import kotlinx.android.synthetic.main.activity_view_posts.*

class ViewPosts : AppCompatActivity() {
    var hasAnswer = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_posts)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val keyChild = intent.getStringExtra("dateTime")
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUserss = FirebaseDatabase.getInstance().reference

        refUserss.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val current_post = p0.child("posts/${firebaseUserID.toString()}/$keyChild")

                            textView16.text = current_post.child("question_in_single_line").value.toString()
                            textView17.text = current_post.child("question_in_brief").value.toString()
                            if(current_post.hasChild("image_uri")){
                                val url = current_post.child("image_uri").value.toString()
                                Picasso.get().load(url).into(imageView5)
                                imageView5.visibility = View.VISIBLE
                            }else{
                                imageView5.visibility = View.GONE
                            }
                if(current_post.hasChild("Answers")){
                    val ans_current_post = current_post.child("Answers")
                    hasAnswer = true
                    textView18.visibility = View.VISIBLE
                    button7.visibility = View.VISIBLE
                    textView19.visibility = View.VISIBLE
                    if(ans_current_post.hasChild("image_uri")){
                        Picasso.get().load(ans_current_post.child("image_uri").toString()).placeholder(R.drawable.cover).into(imageView7)
                        imageView7.visibility = View.VISIBLE
                    }else{
                        imageView7.visibility = View.GONE
                    }
                }else{
                    textView18.visibility = View.GONE
                    button7.visibility = View.GONE
                    textView19.visibility = View.GONE
                }



            }



            override fun onCancelled(p0: DatabaseError) {



            }
        })

    }

}