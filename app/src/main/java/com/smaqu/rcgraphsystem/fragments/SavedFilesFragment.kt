package com.smaqu.rcgraphsystem.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.adapter.FileListAdapter
import com.smaqu.rcgraphsystem.dialogs.FileOptionsDialog
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.models.GraphFile
import com.smaqu.rcgraphsystem.models.SeriesContainer
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.HEIGHT_ABOVE_GROUND_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.LOW_PRESSURE_ALTITUDE_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.PRESSURE_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.TEMPERATURE_ID
import com.smaqu.rcgraphsystem.util.ApplicationPreferences
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import com.smaqu.rcgraphsystem.viewmodel.SavedFilesVM
import kotlinx.android.synthetic.main.fragment_saved_files.view.*
import java.lang.Exception
import java.util.*

class SavedFilesFragment : Fragment() {

    private lateinit var savedFilesVM: SavedFilesVM
    private lateinit var mainVM: MainVM
    private lateinit var fileListAdapter: FileListAdapter
    private lateinit var fragmentHandler: FragmentHandler

    companion object {
        fun create() = SavedFilesFragment()
        private const val TAG = "SavedFilesFragment"

        fun createGraphFileModel(activity: Activity, fileEntity: FileEntity): GraphFile? {
            val applicationPreferences = ApplicationPreferences(activity)

            val graphFile = GraphFile(fileEntity.name)
            val timeList = ArrayList<Double>()
            val heightAboveGroundList = ArrayList<Double>()
            val lowPassAltitudeList = ArrayList<Double>()
            val pressureList = ArrayList<Double>()
            val temperatureList = ArrayList<Double>()

            try {
                val packages: List<String> = fileEntity.data.split(applicationPreferences.getSharedPreferences(
                        ApplicationPreferences.PREF_PACKAGE_KEY,
                        ApplicationPreferences.DEFAULT_PACKAGE_SEPARATOR))

                packages.forEach { singlePackage ->
                    singlePackage.split(applicationPreferences.getSharedPreferences(
                            ApplicationPreferences.PREF_VARIABLE_KEY,
                            ApplicationPreferences.DEFAULT_VARIABLE_SEPARATOR))
                            .map { it.toDouble() }
                            .forEachIndexed { index, dataValues ->
                                when (index) {
                                    0 -> timeList.add(dataValues)
                                    1 -> heightAboveGroundList.add(dataValues)
                                    2 -> lowPassAltitudeList.add(dataValues)
                                    3 -> pressureList.add(dataValues)
                                    4 -> temperatureList.add(dataValues)
                                }
                            }
                }
            } catch (error: Exception) {
                return null
            }


            graphFile.timeSeriesContainer = SeriesContainer(-1, activity.getString(R.string.time), timeList, 1)
            graphFile.heightAboveGroundSeriesContainer = SeriesContainer(HEIGHT_ABOVE_GROUND_ID, activity.getString(R.string.height_above_ground), heightAboveGroundList, 10)
            graphFile.lowPassAltitudeSeriesContainer = SeriesContainer(LOW_PRESSURE_ALTITUDE_ID, activity.getString(R.string.low_pass_altitude), lowPassAltitudeList, 10)
            graphFile.pressureSeriesContainer = SeriesContainer(PRESSURE_ID, activity.getString(R.string.pressure), pressureList, 1000)
            graphFile.temperatureSeriesContainer = SeriesContainer(TEMPERATURE_ID, activity.getString(R.string.temperature), temperatureList, 100)

            return graphFile
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentHandler = context as FragmentHandler
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_saved_files, container, false)
        savedFilesVM = ViewModelProviders.of(this).get(SavedFilesVM::class.java)
        mainVM = ViewModelProviders.of(requireActivity()).get(MainVM::class.java)

        fileListAdapter = FileListAdapter(ArrayList())

        savedFilesVM.filesName.observe(this, Observer {
            it?.let {
                fileListAdapter.updateAdapter(it)
            }
        })

        val recyclerView = view.rv_saved_files
        recyclerView.apply {
            adapter = fileListAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        fileListAdapter.setOnClickListener(object : FileListAdapter.ClickListener {
            override fun onItemClick(fileEntity: FileEntity) {
                mainVM.graphFile.value = createGraphFileModel(requireActivity(), fileEntity)
                if (mainVM.graphFile.value == null) {
                    Toast.makeText(context, "Your data is incompatible with require scheme. Try to fix it.", Toast.LENGTH_LONG).show()
                } else {
                    fragmentHandler.showGraphFragment(false)
                }
            }
        })

        fileListAdapter.setOnLongClickListener(object : FileListAdapter.LongClickListener {
            override fun onLongItemClick(fileEntity: FileEntity): Boolean {
                FileOptionsDialog.create(fileEntity).also {
                    it.show(fragmentManager, FileOptionsDialog.TAG)
                }
                return true
            }
        })
        return view
    }
}