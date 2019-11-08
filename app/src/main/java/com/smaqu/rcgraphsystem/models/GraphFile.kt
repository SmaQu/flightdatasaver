package com.smaqu.rcgraphsystem.models

import java.io.Serializable

data class GraphFile(val fileName: String,
                     var timeSeriesContainer: SeriesContainer? = null,
                     var heightAboveGroundSeriesContainer: SeriesContainer? = null,
                     var lowPassAltitudeSeriesContainer: SeriesContainer? = null,
                     var pressureSeriesContainer: SeriesContainer? = null,
                     var temperatureSeriesContainer: SeriesContainer? = null) : Serializable