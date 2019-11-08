package com.smaqu.rcgraphsystem.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.smaqu.rcgraphsystem.GraphViewExtend
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.util.GraphFileConverter
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.fragment_full_screen_grpah.view.*

class FullScreenGraph : Fragment() {

    companion object {
        const val TAG = "FullScreenGraph"
        fun create() = FullScreenGraph()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_grpah, container, false)

        val mainVM: MainVM = ViewModelProviders.of(requireActivity()).get(MainVM::class.java)
        val graphFileConverter = GraphFileConverter(mainVM)

        val graphView: GraphViewExtend = view.graph_full_screen as GraphViewExtend

        if (mainVM.graphFile.value == null
                || mainVM.graphFile.value?.timeSeriesContainer?.elements == null
                || mainVM.graphFile.value?.timeSeriesContainer?.elements?.isEmpty()!!) {
            return view
        }

        graphView.refreshGraph(graphFileConverter)

        return view
    }
}