package com.teamvpn.devhub

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_about_us.*
import java.util.jar.Manifest

class about_us : AppCompatActivity() {

    val contactNumber = 7795330913
    val emailAddressespats = arrayOf("prathyushathimmapuram@gmail.com")
    val emailAddressesniran = arrayOf("hotkid@niran.dev")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        contact_vallabha.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:"+contactNumber))
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // so if the permission is not granted this statement is executed based on the condition
                ActivityCompat.requestPermissions(this@about_us, arrayOf(android.Manifest.permission.CALL_PHONE),1)
            }else{
                startActivity(intent)
            }

        }
        contact_prathyusha.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("Email")
            intent.putExtra(Intent.EXTRA_EMAIL,emailAddressespats)
            intent.putExtra(Intent.EXTRA_SUBJECT,"Support")
            intent.type = "message/rfc822"
            val mailIntent = Intent.createChooser(intent,"Launch Email")
            startActivity(mailIntent)
        }
        contact_niran.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("Email")
            intent.putExtra(Intent.EXTRA_EMAIL,emailAddressesniran)
            intent.putExtra(Intent.EXTRA_SUBJECT,"Support")
            intent.type = "message/rfc822"
            val mailIntent = Intent.createChooser(intent,"Launch Email")
            startActivity(mailIntent)
        }
    }

    override fun onBackPressed() {

        startActivity(Intent(this@about_us,MainActivity::class.java))
        finish()
    }
}