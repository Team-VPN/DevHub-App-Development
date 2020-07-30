package com.teamvpn.devhub

import android.content.DialogInterface
import android.content.Intent
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
import com.teamvpn.devhub.MainActivity.Companion.vibrator
import com.teamvpn.devhub.ModelClass.MyPostClass
import kotlinx.android.synthetic.main.activity_view_posts.*

class ViewPosts : AppCompatActivity() {
    var hasAnswer = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_posts)
        var uid = ""
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
                if(current_post.hasChild("answers")){
                    val allAnswers = current_post.child("answers").children
                    for (allansUID in allAnswers){
                        val uidOfUser = allansUID.child("uid").value.toString()
                        val emailOfuidOfUser = p0.child("users/$uidOfUser/email").value.toString()
                        uid = uidOfUser
                        button7.text = emailOfuidOfUser
                        val answerToQn = allansUID.child("answer").value.toString()
                        textView19.text = answerToQn
                        textView18.visibility = View.VISIBLE
                        button7.visibility = View.VISIBLE
                        textView19.visibility = View.VISIBLE

                        if(allansUID.hasChild("image_url")){
                            val image_url = allansUID.child("image_url").value.toString()
                            imageView7.visibility = View.VISIBLE
                            Picasso.get().load(image_url).into(imageView7)
                            break
                        }else{
                            break
                        }
                    }
                    hasAnswer = true


                }else{
                    textView18.visibility = View.GONE
                    button7.visibility = View.GONE
                    textView19.visibility = View.GONE
                }



            }



            override fun onCancelled(p0: DatabaseError) {



            }
        })

        button7.setOnClickListener {
            if(uid!=""){
                vibrator.vibrate(60)
                val options = arrayOf<CharSequence>(
                    "Send Message",
                    "Visit Profile"
                )
                val builder: AlertDialog.Builder? =  AlertDialog.Builder(this)
                builder?.setTitle("What do you want to do, bro?")
                builder?.setItems(options, DialogInterface.OnClickListener{ dialog, position ->
                    if(position == 0) {
                            val intent = Intent(this@ViewPosts, MessageChatActivity::class.java)
                            intent.putExtra("visit_id", uid)
                            startActivity(intent)

                    }
                    if(position == 1) {
                        //later
                        // MACHA NIRAN ADD IT HERE DAAAA IF YOU ARE WORKING ON THIS
                        // also do the same in ViewPosts Activity also, i have used the same alert box
                    }

                })

                builder?.show()
            }
        }

    }

}