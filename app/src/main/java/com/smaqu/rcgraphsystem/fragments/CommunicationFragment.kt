package com.smaqu.rcgraphsystem.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.adapter.CommunicationPagerAdapter
import kotlinx.android.synthetic.main.fragment_communication.view.*

class CommunicationFragment : Fragment() {

    companion object {
        fun create() = CommunicationFragment()
        private const val TAG = "CommunicationFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_communication, container, false)

        val communicationPagerAdapter = CommunicationPagerAdapter(fragmentManager!!, context!!)
        val viewPager = view.vp_communication
        viewPager.adapter = communicationPagerAdapter
        view.tab_layout.setupWithViewPager(viewPager)
        return view
    }
}