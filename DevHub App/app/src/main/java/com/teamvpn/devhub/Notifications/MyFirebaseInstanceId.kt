package com.teamvpn.devhub.Notifications

import android.media.session.MediaSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService()
{
    override fun onNewToken(p0: String)
    {
        super.onNewToken(p0)


        val firebaseUser= FirebaseAuth.getInstance().currentUser

        // token for each device to send unique notificaitons

        val refreshToken = FirebaseInstanceId.getInstance().token
        
        
        if(firebaseUser!=null) //refresh the goddamn token every fkkk time or else this shit will behave like crazyyyyyyyy
        {
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser //recognize user using their ID
        //each notification will have a seperate parent node in db as tokens which contains the token ID
        //using this, we can send notifications from Niran to Kaori
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens") // change od database - Token DB is being used
        val token = Token(refreshToken!!)
        ref.child(firebaseUser!!.uid).setValue(token)

    }

    //token for every notification for every message sent from alpha to beta


}