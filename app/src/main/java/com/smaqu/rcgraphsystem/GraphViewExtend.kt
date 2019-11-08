package com.smaqu.rcgraphsystem

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.smaqu.rcgraphsystem.models.SeriesContainer
import com.smaqu.rcgraphsystem.util.GraphFileConverter
import java.text.NumberFormat

class GraphViewExtend : GraphView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    fun refreshGraph(graphFileConverter: GraphFileConverter) {
        super.init()
        this.clearSecondScale()
        this.removeAllSeries()

        val seriesContainerList: List<SeriesContainer> = graphFileConverter.getCheckedYAxisSeries()
        if (seriesContainerList.isEmpty()) {
            return
        }

        val firstSeries = graphFileConverter.createSeries(seriesContainerList[0])
        firstSeries.thickness = 8
        firstSeries.title = seriesContainerList[0].label
        firstSeries.color = Color.BLUE
        this.viewport.setMinX(0.0)
        this.viewport.setMaxX(firstSeries.highestValueX)
        this.viewport.setMinY(firstSeries.lowestValueY)
        this.viewport.setMaxY(firstSeries.highestValueY)
        this.addSeries(firstSeries)

        if (seriesContainerList.size == 2) {
            val secondSeries = graphFileConverter.createSeries(seriesContainerList[1])
            secondSeries.color = Color.RED
            secondSeries.title = seriesContainerList[1].label
            this.secondScale.setMinY(secondSeries.lowestValueY)
            this.secondScale.setMaxY(secondSeries.highestValueY)
            this.secondScale.addSeries(secondSeries)
        }

        setUpGraphStartingSettings()
    }

    private fun setUpGraphStartingSettings() {
        // activate horizontal zooming and scrolling
        this.viewport.isScalable = true

        // activate horizontal and vertical zooming and scrolling
        this.viewport.setScalableY(true)

        // set manual Y bounds
        this.viewport.isYAxisBoundsManual = true
        this.gridLabelRenderer?.textSize = 30f

        val nf = NumberFormat.getInstance()
        nf.minimumFractionDigits = 0
        nf.minimumIntegerDigits = 4

        this.gridLabelRenderer.numHorizontalLabels = 4

        this.gridLabelRenderer.labelFormatter = DefaultLabelFormatter(null, nf)
        this.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        this.legendRenderer.backgroundColor = Color.TRANSPARENT

        this.gridLabelRenderer.verticalLabelsSecondScaleColor = Color.RED
        this.legendRenderer.isVisible = true
    }

}