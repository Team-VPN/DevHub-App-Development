package com.teamvpn.devhub.ModelClass

import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import de.hdodenhof.circleimageview.CircleImageView

data class userss(
    var uid: String? = "",
    var username: String? = "",
    var fullname: String? = "",
    var phoneno: String? = "",
    var email: String? = "",
    var github: String? = "",
    var skills: MutableList<String>,
    var image: String? = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "fullname" to fullname,
            "phoneNumber" to phoneno,
            "email" to email,
            "github" to github,
            "skills" to skills,
            "imageUrl" to image
        )
    }
}