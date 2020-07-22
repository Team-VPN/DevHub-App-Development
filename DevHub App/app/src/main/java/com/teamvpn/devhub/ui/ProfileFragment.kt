package com.teamvpn.devhub.ui

import android.os.Bundle
import android.text.LoginFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.teamvpn.devhub.MainActivity
import com.teamvpn.devhub.MainActivity.Companion.myuserClass
import com.teamvpn.devhub.R
import com.teamvpn.devhub.User
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text


class ProfileFragment : Fragment() {
    lateinit var user: FirebaseUser
    //Creating member variables
     var mFirebaseDatabase: DatabaseReference?=null
     var mFirebaseInstance: FirebaseDatabase?=null
    var userId:String?=null
    lateinit var username:TextView
    lateinit var email:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
         username = view.findViewById<TextView>(R.id.textView)
         email = view.findViewById<TextView>(R.id.textView4)

        return view
    }





}