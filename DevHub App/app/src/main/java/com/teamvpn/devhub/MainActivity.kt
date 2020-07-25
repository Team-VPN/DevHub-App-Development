package com.teamvpn.devhub

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.teamvpn.devhub.ModelClass.MyUserClass
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*


data class User(
    var uid:String, var username: String, var fullname: String, var sex:String, var dob: String, var phoneNumber: String, var email: String,
    var skills:MutableList<String>,
    var image_url:String)


class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var auth:FirebaseAuth
    var clearAllBackActivities_status = false
    companion object{
        private var firebaseAuth: FirebaseAuth? = null
        var mAuthListener: FirebaseAuth.AuthStateListener? = null
        lateinit var vibrator: Vibrator
        lateinit var database: DatabaseReference
        lateinit var auth:FirebaseAuth
        var mStorageRef: StorageReference? = null
        lateinit var myuserClass:User

    }

    lateinit var user:FirebaseUser
    //Creating member variables
    private var mFirebaseDatabase: DatabaseReference?=null
    private var mFirebaseInstance: FirebaseDatabase?=null

    var userId:String?=null

    var refUsers: DatabaseReference? = null
    //var refUsersMain: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //window.statusBarColor = Color.WHITE
        // to customise the toolbar
        toolbar.setTitleTextColor(Color.rgb(98, 0, 238)) // will set the text color
        toolbar.title = "DevHub" // title for the toolbar
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }
        database = FirebaseDatabase.getInstance().getReference("posts")
        mStorageRef = FirebaseStorage.getInstance().getReference().child("images_in_posts")
        val toolbarchat = findViewById<AppCompatImageButton>(R.id.toolbarchat)
        toolbarchat.setOnClickListener {
            val intent = Intent(this, MainChat::class.java)
            startActivity(intent)
        }
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_feed, R.id.navigation_post_qns, R.id.navigation_near_by_location_developers, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: MyUserClass? = p0.getValue(MyUserClass::class.java)
                    val c = user!!.getFullName()
                   // Log.d("DEBUGGING","$c")
                   // textView3.text = user.getUserName()
                    //Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(cirprofile)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUGGING","Data cancelled in mainactivity")
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.editProfile ->{
                startActivity(Intent(this@MainActivity, EditProfile::class.java))
                finish()
            }
            R.id.About_us->{
                startActivity(Intent(this@MainActivity, about_us::class.java))

            }
            R.id.logout->{
                val alertBox = AlertDialog.Builder(this@MainActivity)
                alertBox.setTitle("Are you sure you want to logout from the app?")
                alertBox.setIcon(R.mipmap.ic_launcher)
                alertBox.setMessage("You may have to login again to use the application. Do you wish to continue ?")
                alertBox.setCancelable(true)
                alertBox.setPositiveButton("Logout"){_,_->
                    FirebaseAuth.getInstance().signOut()
                    Toasty.success(this@MainActivity,"You have successfully logged out", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                    startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                    finish()
                }
                alertBox.setNegativeButton("stay here"){_,_->

                }
                alertBox.create().show()
            }
            R.id.exit->{
                val alertBox = AlertDialog.Builder(this@MainActivity)
                alertBox.setTitle("Do you wish to exit from the app?")
                alertBox.setIcon(R.mipmap.ic_launcher)
                alertBox.setMessage("By clicking Exit, the app will be closed")
                alertBox.setCancelable(true)
                alertBox.setPositiveButton("Exit"){_,_->
                    finish()
                }
                alertBox.setNegativeButton("No"){_,_->

                }
                alertBox.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null){
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }
    }

     fun changeFragment(){
        NavigationUI.navigateUp(navController,null)
    }
}