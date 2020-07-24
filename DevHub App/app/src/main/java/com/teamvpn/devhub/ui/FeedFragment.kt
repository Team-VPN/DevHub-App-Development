package com.teamvpn.devhub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamvpn.devhub.R
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //viewManager = LinearLayoutManager(this)
        //viewAdapter = MyAdapter(myDataset)

       // recycler_view.setHasFixedSize(true)
       // recycler_view.layoutManager = viewManager
       // recycler_view.adapter = viewAdapter

        return inflater.inflate(R.layout.fragment_feed, container, false)
    }
}