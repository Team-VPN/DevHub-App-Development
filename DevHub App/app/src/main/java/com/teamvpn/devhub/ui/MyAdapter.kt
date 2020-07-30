package com.teamvpn.devhub.ui

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.MainActivity.Companion.vibrator
import com.teamvpn.devhub.MakeAnswers
import com.teamvpn.devhub.MakeAnswers.Companion.companionChild
import com.teamvpn.devhub.MakeAnswers.Companion.companionUID
import com.teamvpn.devhub.ModelClass.PostClass
import com.teamvpn.devhub.R
import com.teamvpn.devhub.SayAnswer
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_view_posts_recycler_view.view.*


class MyAdapter(mContext: Context, myDataset: List<PostClass>):
        RecyclerView.Adapter<MyAdapter.MyviewHolder>() {

    class MyviewHolder(val mItemView: View):RecyclerView.ViewHolder(mItemView){
        var likeBtn = mItemView.imageButton
        var commentBtn = mItemView.imageButton2
        var shareBtn = mItemView.imageButton3
        var state_of_likeBtn = true
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

        MyHoder.itemView.setOnClickListener{
            val intent = Intent(mContext, MakeAnswers::class.java)
            intent.putExtra("GetUserID",posts.uidOfPoster)
            intent.putExtra("dateTime", posts.date_time)
            mContext.startActivity(intent)
        }

        MyHoder.likeBtn.setOnClickListener {
            MediaPlayer.create(mContext,R.raw.like_button).start()
            if(MyHoder.state_of_likeBtn){
                MyHoder.likeBtn.setImageResource(R.drawable.like_button)
                MyHoder.state_of_likeBtn = false
            }else{
                MyHoder.likeBtn.setImageResource(R.drawable.ic_baseline_thumb_up_24)
                MyHoder.state_of_likeBtn = true
            }

        }
        MyHoder.commentBtn.setOnClickListener {
            vibrator.vibrate(60)
            val intent = Intent(mContext, SayAnswer::class.java)
            companionUID = posts.uidOfPoster.toString()
            companionChild = posts.date_time.toString()
            intent.putExtra("GetUserID",posts.uidOfPoster.toString())
            intent.putExtra("dateTime", posts.date_time.toString())
            mContext.startActivity(intent)
        }

        MyHoder.shareBtn.setOnClickListener {
            vibrator.vibrate(60)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey there, I am pleased with the joy of learning with DevHub.\n\nJoin DevHub app today for free and have unlimited benefits. https://www.playstore.com/com.someurl.test")
            sendIntent.type = "text/plain"
             mContext.startActivity(sendIntent)

        }
    }

}