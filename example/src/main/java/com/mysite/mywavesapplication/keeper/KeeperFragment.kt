package com.mysite.mywavesapplication.keeper


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mysite.mywavesapplication.R

/**
 * A simple [Fragment] subclass.
 */
class KeeperFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keeper, container, false)
    }

    companion object {
        fun newInstance(): KeeperFragment {
            return KeeperFragment()
        }
    }
}
