package com.teamvpn.devhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.mfragment.ChatsFragment
import com.teamvpn.devhub.mfragment.SearchFragment
import com.google.firebase.database.FirebaseDatabase
import com.teamvpn.devhub.ModelClass.Chat
import com.teamvpn.devhub.ModelClass.Chatlist
import com.teamvpn.devhub.mfragment.SettingFragment
import kotlinx.android.synthetic.main.activity_main_chat.*
/* I dont know how, but this is working, as of now.
Need to check how this works and understand for future reference */
class MainChat : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    //var refUsersMain: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)
        setSupportActionBar(toolbar1)
        
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("ChatUsersDB").child(firebaseUser!!.uid)
        //refUsersMain = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)//unstable
        //need to see if data can be seperated so that it can help the work get done faster

        val toolbar1: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar1)
        supportActionBar!!.title=""

        val tabLayout: TabLayout = findViewById(R.id.tablyt)
        val viewPager: ViewPager = findViewById(R.id.view_pager1)
       //

       // viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
       // viewPagerAdapter.addFragment(SearchFragment(), "Search")
       // viewPagerAdapter.addFragment(SettingFragment(), "Settings")

       // viewPager.adapter = viewPagerAdapter
       // tabLayout.setupWithViewPager(viewPager)
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {

                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages = 0

                for(dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen())
                    {
                        countUnreadMessages += 1
                    }
                }

                if(countUnreadMessages == 0)
                {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatsFragment(), "【$countUnreadMessages】Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingFragment(), "Settings")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)

            }

            override fun onCancelled(error: DatabaseError) {



            }
        })




        //profile picture crap
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)
                    username1.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(cirprofile)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager)
    {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }
    }

    private fun updateStstus(status: String)
    {
        val ref = FirebaseDatabase.getInstance() .reference.child("ChatUsersDB").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()
        updateStstus("online")
    }

    override fun onPause() {
        super.onPause()

        updateStstus("offline")
    }

}