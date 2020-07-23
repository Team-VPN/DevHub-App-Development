package com.teamvpn.devhub.mfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.R
import kotlinx.android.synthetic.main.fragment_setting.view.*


/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment()
{
    var UserReference : DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        UserReference = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)

        UserReference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)

                    if(context!=null)
                    {
                        view.username_settings.text= user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user.getCover()).into(view.cover_image_settings)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {



            }

        })




        return view
    }

}