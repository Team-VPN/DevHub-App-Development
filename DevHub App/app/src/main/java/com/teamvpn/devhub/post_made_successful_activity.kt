package com.teamvpn.devhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.teamvpn.devhub.MainActivity.Companion.vibrator
import com.teamvpn.devhub.ModelClass.MyPostClass
import kotlinx.android.synthetic.main.activity_post_made_successful_activity.*

class post_made_successful_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_made_successful_activity)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var count = 0
        val refUsers = FirebaseDatabase.getInstance().reference
        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("users/$firebaseUserID/Questions_count")){
                    count = p0.child("users/$firebaseUserID/Questions_count").getValue(Int::class.java)!!
                    Toast.makeText(this@post_made_successful_activity,count,Toast.LENGTH_SHORT).show()
                }
            }



            override fun onCancelled(p0: DatabaseError) {



            }
        })


        // button to go back to previous activity
        button5.setOnClickListener{
            vibrator.vibrate(60)
            
            finish()
        }
    }
}