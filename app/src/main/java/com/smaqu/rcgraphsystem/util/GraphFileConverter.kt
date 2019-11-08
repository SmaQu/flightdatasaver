package com.smaqu.rcgraphsystem.util

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.smaqu.rcgraphsystem.models.SeriesContainer
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.HEIGHT_ABOVE_GROUND_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.LOW_PRESSURE_ALTITUDE_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.NO_Y_AXIS
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.PRESSURE_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.TEMPERATURE_ID
import com.smaqu.rcgraphsystem.viewmodel.MainVM

class GraphFileConverter(val mainVM: MainVM) {

    fun setCheckSeries(seriesId: Int, isChecked: Boolean): Boolean {
        val seriesContainer = getSeriesContainerById(seriesId)
        seriesContainer.isChecked = isChecked
        return seriesContainer.yAxis != NO_Y_AXIS
    }

    fun getCheckedYAxisSeries(): List<SeriesContainer> {
        val seriesContainerQueue: ArrayList<SeriesContainer> = ArrayList()
        val heightSeriesContainer = getSeriesContainerById(HEIGHT_ABOVE_GROUND_ID)
        val lowPassSeriesContainer = getSeriesContainerById(LOW_PRESSURE_ALTITUDE_ID)
        val pressureSeriesContainer = getSeriesContainerById(PRESSURE_ID)
        val temperatureSeriesContainer = getSeriesContainerById(TEMPERATURE_ID)

        if (heightSeriesContainer.yAxis != NO_Y_AXIS
                && heightSeriesContainer.isChecked) {
            seriesContainerQueue.add(heightSeriesContainer)
        }
        if (lowPassSeriesContainer.yAxis != NO_Y_AXIS
                && lowPassSeriesContainer.isChecked) {
            seriesContainerQueue.add(lowPassSeriesContainer)
        }
        if (pressureSeriesContainer.yAxis != NO_Y_AXIS
                && pressureSeriesContainer.isChecked) {
            seriesContainerQueue.add(pressureSeriesContainer)
        }
        if (temperatureSeriesContainer.yAxis != NO_Y_AXIS
                && temperatureSeriesContainer.isChecked) {
            seriesContainerQueue.add(temperatureSeriesContainer)
        }
        return seriesContainerQueue
    }

    fun removeOldUsedAxis(newAxis: Int) {
        val heightSeriesContainer = getSeriesContainerById(HEIGHT_ABOVE_GROUND_ID)
        val lowPassSeriesContainer = getSeriesContainerById(LOW_PRESSURE_ALTITUDE_ID)
        val pressureSeriesContainer = getSeriesContainerById(PRESSURE_ID)
        val temperatureSeriesContainer = getSeriesContainerById(TEMPERATURE_ID)

        if (heightSeriesContainer.yAxis == newAxis) {
            heightSeriesContainer.yAxis = SeriesContainer.NO_Y_AXIS
        }
        if (lowPassSeriesContainer.yAxis == newAxis) {
            lowPassSeriesContainer.yAxis = SeriesContainer.NO_Y_AXIS
        }
        if (pressureSeriesContainer.yAxis == newAxis) {
            pressureSeriesContainer.yAxis = SeriesContainer.NO_Y_AXIS
        }
        if (temperatureSeriesContainer.yAxis == newAxis) {
            temperatureSeriesContainer.yAxis = SeriesContainer.NO_Y_AXIS
        }
    }

    fun setNewAxis(seriesId: Int, newAxis: Int): Boolean {
        val seriesContainer = getSeriesContainerById(seriesId)
        if (seriesContainer.yAxis == newAxis){
            return true
        }
        seriesContainer.yAxis = newAxis
        return false
    }

    fun createSeries(seriesContainer: SeriesContainer): LineGraphSeries<DataPoint> {
        val timeSeriesValues = mainVM.graphFile.value!!.timeSeriesContainer?.elements!!
        val generateGraphSeries = ArrayList<DataPoint>()

        timeSeriesValues.forEachIndexed { index, it ->
            generateGraphSeries.add(DataPoint(index.toDouble(), seriesContainer.elements[index]))
        }

        return LineGraphSeries(generateGraphSeries.toTypedArray())
    }

    private fun getSeriesContainerById(seriesId: Int): SeriesContainer {
        var seriesContainer: SeriesContainer? = null
        when (seriesId) {
            HEIGHT_ABOVE_GROUND_ID -> {
                seriesContainer = mainVM.graphFile.value?.heightAboveGroundSeriesContainer
            }
            LOW_PRESSURE_ALTITUDE_ID -> {
                seriesContainer = mainVM.graphFile.value?.lowPassAltitudeSeriesContainer
            }
            PRESSURE_ID -> {
                seriesContainer = mainVM.graphFile.value?.pressureSeriesContainer
            }
            TEMPERATURE_ID -> {
                seriesContainer = mainVM.graphFile.value?.temperatureSeriesContainer
            }
        }
        return seriesContainer!!
    }
}