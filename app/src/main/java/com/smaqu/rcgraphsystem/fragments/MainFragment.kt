package com.smaqu.rcgraphsystem.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment(),
        View.OnClickListener {

    private lateinit var fragmentHandler: FragmentHandler

    companion object {
        fun create() = MainFragment()
        private const val TAG = "MainFragment"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentHandler = context as FragmentHandler
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)

        view.cv_data_request.setOnClickListener(this)
        view.cv_graph.setOnClickListener(this)
        view.cv_saved.setOnClickListener(this)
        view.cv_settings.setOnClickListener(this)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.item_search_device -> {
                fragmentHandler.showPairedDevicesFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            cv_data_request.id -> {
                fragmentHandler.showCommunicationFragment()
            }
            cv_graph.id -> {
                fragmentHandler.showGraphFragment(false)
            }
            cv_saved.id -> {
                fragmentHandler.showSavedFilesFragment(false)
            }
            cv_settings.id -> {
                fragmentHandler.showSettingsFragment()
            }
        }
    }
}