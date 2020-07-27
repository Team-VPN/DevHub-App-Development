package com.teamvpn.devhub.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.teamvpn.devhub.AdapterClasses.UserAdapter
import com.teamvpn.devhub.MainActivity.Companion.auth
import com.teamvpn.devhub.ModelClass.PostClass
import com.teamvpn.devhub.ModelClass.Users
import com.teamvpn.devhub.R


class FeedFragment : Fragment() {
    private lateinit var recyclerView_posts: RecyclerView
    private lateinit var posts:List<PostClass>
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_feed, container, false)
        recyclerView_posts = view.findViewById(R.id.recycler_view)
        recyclerView_posts.setHasFixedSize(false)
        recyclerView_posts.layoutManager = LinearLayoutManager(context)
        recyclerView_posts.addItemDecoration(DividerItemDecoration(recyclerView_posts.context, DividerItemDecoration.VERTICAL))
        posts = ArrayList()

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference
        val email = FirebaseAuth.getInstance().currentUser?.email

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("posts/${firebaseUserID.toString()}")){
                    for (snapshot in p0.child("posts/${firebaseUserID.toString()}").children) {
                        if(p0.hasChild("posts/${firebaseUserID.toString()}")){
                            val single_qn = snapshot.child("question_in_single_line").value.toString()
                            val multi_line_qn = snapshot.child("question_in_brief").value.toString()
                            val uidOfPoster = snapshot.child("uid").value.toString()
                            val profile_pic_url = p0.child("users/$uidOfPoster/image_url").value.toString()
                            (posts as ArrayList<PostClass>).add(PostClass(single_qn,multi_line_qn,profile_pic_url))
                            recyclerView_posts.adapter?.notifyDataSetChanged()
                        }
                    }
                }else{
                    val somename = p0.child("users/$firebaseUserID/fullname").getValue().toString()
                    (posts as ArrayList<PostClass>).add(PostClass("Hey!, $somename","We welcome you to our developer community. we expect you to not only post questions and get answers, also need to answer the questions.\nIts time to reskill india.\nJai Hindh, Team VPN, DEVHUB APP DEVELOPERS","https://avatars1.githubusercontent.com/u/68112921?s=400&u=afc1248f8a9f6fea26118be27d9d1a0475ac5cfd&v=4"))
                        recyclerView_posts.adapter?.notifyDataSetChanged()
                }

                }



            override fun onCancelled(p0: DatabaseError) {



            }
        })



        recyclerView_posts.adapter = context?.let { MyAdapter(it,posts) }
    return view
    }
}