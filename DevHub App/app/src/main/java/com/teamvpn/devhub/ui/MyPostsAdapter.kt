package com.teamvpn.devhub.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.teamvpn.devhub.ModelClass.MyPostClass
import com.teamvpn.devhub.R
import com.teamvpn.devhub.ViewPosts
import com.teamvpn.devhub.ui.ProfileFragment.Companion.NOTIFY_RECYCLER_VIEW
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.my_questions_item.view.*

class MyPostsAdapter(mContext: Context, myDataset: List<MyPostClass>):
    RecyclerView.Adapter<MyPostsAdapter.MyviewHolder>() {

    class MyviewHolder(val mItemView: View): RecyclerView.ViewHolder(mItemView){
        var deleteBtn = mItemView.imageButton4
        var status:TextView
        var My_question:TextView
        //var deleteButton:Button
        init {
            status =  mItemView.findViewById(R.id.textView15)
            My_question = mItemView.findViewById(R.id.textView14)
          //  deleteButton = mItemView.findViewById(R.id.imageButton4)
        }
    }

    private val mContext: Context
    private var myDataset:List<MyPostClass> = listOf()

    init {
        this.myDataset = myDataset
        this.mContext = mContext
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyPostsAdapter.MyviewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.my_questions_item, viewGroup, false)
        return MyPostsAdapter.MyviewHolder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(MyHoder: MyPostsAdapter.MyviewHolder, i: Int) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference
        val posts: MyPostClass = myDataset[i]
        MyHoder.My_question.text = posts.qn_in_aSen
        MyHoder.status.text = posts.status
        MyHoder.deleteBtn.setOnClickListener {
            val alertBox = AlertDialog.Builder(mContext)
            alertBox.setTitle("Do you really want to delete your post?")
            alertBox.setIcon(R.mipmap.ic_launcher)
            alertBox.setMessage("The process cannot be reversed,if pressed yes")
            alertBox.setCancelable(true)
            alertBox.setPositiveButton("yes") { _, _ ->
                refUsers.child("posts/$firebaseUserID/${posts.date_time}").removeValue()
                Toasty.success(mContext,"Your post is deleted successfully, click on profile button again to view updated data",Toast.LENGTH_SHORT).show()
                NOTIFY_RECYCLER_VIEW = true
                // Reload current fragment
                Navigation.createNavigateOnClickListener(R.id.navigation_profile, null)
                MyHoder.itemView.visibility = View.GONE
            }
            alertBox.setNegativeButton("No") { _, _ ->

            }
            alertBox.create().show()
        }
        MyHoder.itemView.setOnClickListener {
            val intent = Intent(mContext,ViewPosts::class.java)
            intent.putExtra("dateTime", posts.date_time)
            mContext.startActivity(intent)
        }





    }

}