package com.smaqu.rcgraphsystem.fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.smaqu.rcgraphsystem.GraphViewExtend
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.adapter.GraphOptionsAdapter
import com.smaqu.rcgraphsystem.dialogs.PickAxisDialog
import com.smaqu.rcgraphsystem.dialogs.PickAxisDialog.Companion.ARG_NEW_AXIS
import com.smaqu.rcgraphsystem.dialogs.PickAxisDialog.Companion.ARG_SERIES_ID
import com.smaqu.rcgraphsystem.dialogs.PickAxisDialog.Companion.AXIS_REQUEST_CODE
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import com.smaqu.rcgraphsystem.models.GraphFile
import com.smaqu.rcgraphsystem.models.SeriesContainer
import com.smaqu.rcgraphsystem.util.GraphFileConverter
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import kotlinx.android.synthetic.main.content_graph.view.*
import kotlinx.android.synthetic.main.fragment_graph.view.*


class GraphFragment : Fragment() {
    private lateinit var mainVM: MainVM
    private lateinit var graphFileConverter: GraphFileConverter
    private lateinit var fragmentHandler: FragmentHandler

    private lateinit var graphView: GraphViewExtend
    private lateinit var fileNameTv: TextView
    private lateinit var fullScreenFab: FloatingActionButton

    private lateinit var adapter: GraphOptionsAdapter

    companion object {
        const val TAG = "GraphFragment"
        fun create() = GraphFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentHandler = context as FragmentHandler
        setHasOptionsMenu(true)
    }
    // time, hag, tlpa, press, temp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_graph, container, false)
        mainVM = ViewModelProviders.of(requireActivity()).get(MainVM::class.java)
        graphFileConverter = GraphFileConverter(mainVM)

        val recyclerView = view.recycler_graph_options
        graphView = view.graph_main as GraphViewExtend
        fileNameTv = view.text_file_name
        fullScreenFab = view.fab_full_screen
        fullScreenFab.setOnClickListener {
            fragmentHandler.showFullScreenGraph()
        }

        if (mainVM.graphFile.value == null
                || mainVM.graphFile.value?.timeSeriesContainer?.elements == null
                || mainVM.graphFile.value?.timeSeriesContainer?.elements?.isEmpty()!!) {
            return view
        }

        val adapterList = buildAdapterList(mainVM.graphFile.value!!)
        adapter = GraphOptionsAdapter(adapterList)

        recyclerView.also {
            it.adapter = adapter
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(context)
        }

        adapter.setOnClickListener(object : GraphOptionsAdapter.OnClickListener {
            override fun onItemClick(seriesContainer: SeriesContainer) {
                openPickAxisDialog(seriesContainer)
            }

            override fun onCheckBoxClick(seriesId: Int, isChecked: Boolean) {
                val isUsed = graphFileConverter.setCheckSeries(seriesId, isChecked)
                if (isUsed) {
                    graphView.refreshGraph(graphFileConverter)
                }
            }
        })

        setUpGraphTittle()
        graphView.refreshGraph(graphFileConverter)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.graph_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.item_show_files -> {
                fragmentHandler.showSavedFilesFragment(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PickAxisDialog.AXIS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val newAxis = data!!.getIntExtra(ARG_NEW_AXIS, -1)
                    val seriesId = data.getIntExtra(ARG_SERIES_ID, -1)

                    graphFileConverter.removeOldUsedAxis(newAxis)
                    val theSameAxis = graphFileConverter.setNewAxis(seriesId, newAxis)
                    if (!theSameAxis) {
                        adapter.notifyNeData(buildAdapterList(mainVM.graphFile.value!!))
                        graphView.refreshGraph(graphFileConverter)
                    }
                }
            }
        }
    }

    private fun openPickAxisDialog(seriesContainer: SeriesContainer) {
        PickAxisDialog.create(seriesContainer).also {
            it.setTargetFragment(this@GraphFragment, AXIS_REQUEST_CODE)
            it.show(fragmentManager, PickAxisDialog.TAG)
        }
    }

    private fun setUpGraphTittle() {
        mainVM.graphFile.value?.fileName?.let {
            fileNameTv.text = it
        }
    }

    private fun buildAdapterList(graphFile: GraphFile): List<SeriesContainer> {
        val list: ArrayList<SeriesContainer> = ArrayList()
        list.add(graphFile.heightAboveGroundSeriesContainer!!)
        list.add(graphFile.lowPassAltitudeSeriesContainer!!)
        list.add(graphFile.pressureSeriesContainer!!)
        list.add(graphFile.temperatureSeriesContainer!!)
        return list
    }
}