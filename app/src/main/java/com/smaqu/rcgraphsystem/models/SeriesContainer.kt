package com.smaqu.rcgraphsystem.models

import java.io.Serializable

data class SeriesContainer(val seriesId: Int,
                           val label: String,
                           val elements: List<Double>,
                           val scale: Int): Serializable {

    var yAxis: Int = NO_Y_AXIS
    var isChecked = false

    companion object {
        const val NO_Y_AXIS = 0
        const val FIRST_Y_AXIS = 1
        const val SECOND_Y_AXIS = 2

        const val HEIGHT_ABOVE_GROUND_ID = 0
        const val LOW_PRESSURE_ALTITUDE_ID = 1
        const val PRESSURE_ID = 2
        const val TEMPERATURE_ID = 3
    }
}