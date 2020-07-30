package com.teamvpn.devhub

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.Users
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity()
{
    private var userVisitId: String = ""
    var user: Users? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)
        userVisitId = intent.getStringExtra("visit_id")

        val ref  = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(userVisitId)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    user = p0.getValue(Users::class.java)

                    username_display.text = user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).into(profile_display)
                    Picasso.get().load(user!!.getCover()).into(cover_display)

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }


        })

        link_display.setOnClickListener {
            val uri = Uri.parse(user!!.getLinkedin())

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }

        git_display.setOnClickListener {
            val uri = Uri.parse(user!!.getGithub())

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }

        stack_display.setOnClickListener {
            val uri = Uri.parse(user!!.getStackof())

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }

        send_msg_btn.setOnClickListener{

            val intent = Intent(this@VisitUserProfileActivity , MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.getUID())
            startActivity(intent)
        }

    }
}