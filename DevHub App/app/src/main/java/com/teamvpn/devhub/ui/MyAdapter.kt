package com.teamvpn.devhub.ui

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val myDataset:Array<String>):
        RecyclerView.Adapter<MyAdapter.MyviewHolder>() {
    class MyviewHolder(val textView : TextView):RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}