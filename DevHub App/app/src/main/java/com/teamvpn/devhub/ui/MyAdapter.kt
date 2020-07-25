package com.teamvpn.devhub.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.ModelClass.PostClass
import com.teamvpn.devhub.R
import de.hdodenhof.circleimageview.CircleImageView

class MyAdapter(mContext: Context, myDataset: List<PostClass>):
        RecyclerView.Adapter<MyAdapter.MyviewHolder>() {

    class MyviewHolder(val mItemView: View):RecyclerView.ViewHolder(mItemView){
        var singleLineqn:TextView
        var multiLineqn:TextView
        var profileImg:CircleImageView

        init {
            singleLineqn =  mItemView.findViewById(R.id.textView6)
            multiLineqn = mItemView.findViewById(R.id.multilineQn)
            profileImg = mItemView.findViewById(R.id.profilePic_in_posts)
        }
    }

    private val mContext:Context
    private var myDataset:List<PostClass> = listOf()

    init {
        this.myDataset = myDataset
        this.mContext = mContext
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyAdapter.MyviewHolder {
        val view:View = LayoutInflater.from(mContext).inflate(R.layout.item_view_posts_recycler_view, viewGroup, false)
        return MyAdapter.MyviewHolder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(MyHoder: MyAdapter.MyviewHolder, i: Int) {
        val posts: PostClass = myDataset[i]
        MyHoder.singleLineqn.text = posts.SingleLineQn
        MyHoder.multiLineqn.text = posts.MultilineQn
        Picasso.get().load(posts.imageURL).placeholder(R.drawable.profile).into(MyHoder.profileImg)
    }

}