package com.teamvpn.devhub.ui

import android.os.Bundle
import android.text.LoginFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.MainActivity
import com.teamvpn.devhub.MainActivity.Companion.myuserClass
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.NewUserInfo
import com.teamvpn.devhub.R
import com.teamvpn.devhub.User
import kotlinx.android.synthetic.main.activity_main_chat.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text


class ProfileFragment : Fragment() {
    lateinit var user: FirebaseUser
    //Creating member variables
    var refUsers: DatabaseReference? = null
    //var refUsersMain: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null
    var userId:String?=null
    lateinit var username:TextView
    lateinit var email:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
         username = view.findViewById<TextView>(R.id.textView3)
         email = view.findViewById<TextView>(R.id.textView4)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)
                    val c = user!!.getUserName()
                    Log.d("DEBUGGING","$c")
                    username.text = user.getUserName()
                    //Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(cirprofile)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUGGING","Data cancelled")
            }
        })

        return view
    }





}