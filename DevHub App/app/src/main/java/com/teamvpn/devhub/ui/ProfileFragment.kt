package com.teamvpn.devhub.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.MyPostClass
import com.teamvpn.devhub.ModelClass.PostClass
import com.teamvpn.devhub.R
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment() {
    companion object{
        var NOTIFY_RECYCLER_VIEW = false
    }
    var answerCount = 0
    var count = 0
    private lateinit var recyclerView_posts: RecyclerView
    private lateinit var posts:List<MyPostClass>
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var user: FirebaseUser
    //Creating member variables
    var refUsers: DatabaseReference? = null
    //var refUsersMain: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null
    var userId:String?=null
    lateinit var username:TextView
    lateinit var email:TextView
    lateinit var profileImage:CircleImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
         username = view.findViewById<TextView>(R.id.textView3)
         email = view.findViewById<TextView>(R.id.textView4)
        profileImage = view.findViewById<CircleImageView>(R.id.profile_image)
        var QuestionsAsked = view.findViewById<TextView>(R.id.textView2)
        var QuestionsAnswered = view.findViewById<TextView>(R.id.textView12)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)


        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    username.text = p0.child("username").getValue().toString()
                    email.text = p0.child("email").getValue().toString()
                    val imageUrl = p0.child("image_url").getValue().toString()
                    if(imageUrl!=""){
                          Picasso.get().load(imageUrl).placeholder(R.drawable.vector_asset_profile_icon_24).into(profileImage)
                        }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUGGING","Data cancelled in profile fragment")
            }
        })


        recyclerView_posts = view.findViewById(R.id.MyRecyclerView)
        recyclerView_posts.isNestedScrollingEnabled = true
        recyclerView_posts.setHasFixedSize(false)
        recyclerView_posts.layoutManager = LinearLayoutManager(context)
        recyclerView_posts.addItemDecoration(DividerItemDecoration(recyclerView_posts.context, DividerItemDecoration.VERTICAL))
        posts = ArrayList()
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUserss = FirebaseDatabase.getInstance().reference

        val checkAnswersCount = FirebaseDatabase.getInstance().reference

        checkAnswersCount.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(ssnapshot: DataSnapshot) {
                if(ssnapshot.hasChild("posts")){
                    for (snapshot in ssnapshot.child("posts").children) {
                        for(childSnapshot in snapshot.children){
                            val skillsArraySnapshot = childSnapshot.child("answers").children
                            for (skillsArray in skillsArraySnapshot){
                                answerCount = answerCount + 1
                                QuestionsAnswered.text = answerCount.toString()
                            }

                        }


                    }
                }
            }

        } )

        refUserss.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("posts/${firebaseUserID.toString()}")){
                    for (snapshot in p0.child("posts/${firebaseUserID.toString()}").children) {
                        if(p0.hasChild("posts/${firebaseUserID.toString()}")){
                            count += 1
                            val datetime = snapshot.key.toString()
                            QuestionsAsked.text = count.toString()
                            val single_qn = snapshot.child("question_in_single_line").value.toString()
                            if(snapshot.hasChild("answers")){
                                (posts as ArrayList<MyPostClass>).add(MyPostClass(datetime,single_qn,"Answered"))
                                recyclerView_posts.adapter?.notifyDataSetChanged()
                            }else{
                                (posts as ArrayList<MyPostClass>).add(MyPostClass(datetime,single_qn,"Not Answered"))
                                recyclerView_posts.adapter?.notifyDataSetChanged()
                            }
                            //val multi_line_qn = snapshot.child("question_in_brief").value.toString()
                            //val uidOfPoster = snapshot.child("uid").value.toString()
                            //val profile_pic_url = p0.child("users/$uidOfPoster/image_url").value.toString()
                        }
                    }
                }

            }



            override fun onCancelled(p0: DatabaseError) {



            }
        })

        recyclerView_posts.adapter = context?.let { MyPostsAdapter(it,posts) }

        return view
    }



}