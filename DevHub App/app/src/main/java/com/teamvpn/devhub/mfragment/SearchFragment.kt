package com.teamvpn.devhub.mfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamvpn.devhub.AdapterClasses.UserAdapter
import com.teamvpn.devhub.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.

 */
class SearchFragment : Fragment() {
private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View =  inflater.inflate(R.layout.fragment_search, container, false)

        return view
    }


}