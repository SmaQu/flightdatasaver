package com.smaqu.rcgraphsystem.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.models.SeriesContainer
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.FIRST_Y_AXIS
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.HEIGHT_ABOVE_GROUND_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.LOW_PRESSURE_ALTITUDE_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.NO_Y_AXIS
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.PRESSURE_ID
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.SECOND_Y_AXIS
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.TEMPERATURE_ID
import kotlinx.android.synthetic.main.item_graph_options.view.*

class GraphOptionsAdapter(private var list: List<SeriesContainer>) : RecyclerView.Adapter<GraphOptionsAdapter.ViewHolder>() {

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onItemClick(seriesContainer: SeriesContainer)

        fun onCheckBoxClick(seriesId: Int, isChecked: Boolean)
    }

    private var onClickListener: OnClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_graph_options, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var optionTitle = context.resources.getString(R.string.data_value)
        when (list[position].seriesId) {
            HEIGHT_ABOVE_GROUND_ID -> {
                optionTitle = context.resources.getString(R.string.height_above_ground)
            }
            PRESSURE_ID -> {
                optionTitle = context.resources.getString(R.string.pressure)
            }
            LOW_PRESSURE_ALTITUDE_ID -> {
                optionTitle = context.resources.getString(R.string.low_pass_altitude)
            }
            TEMPERATURE_ID -> {
                optionTitle = context.resources.getString(R.string.temperature)
            }
        }

        var optionImage = ContextCompat.getDrawable(context, R.drawable.ic_exposure_zero_24dp)

        when (list[position].yAxis) {
            NO_Y_AXIS -> {
                optionImage = ContextCompat.getDrawable(context, R.drawable.ic_exposure_zero_24dp)
            }
            FIRST_Y_AXIS -> {
                optionImage = ContextCompat.getDrawable(context, R.drawable.ic_exposure_plus_1_24dp)
            }
            SECOND_Y_AXIS -> {
                optionImage = ContextCompat.getDrawable(context, R.drawable.ic_exposure_plus_2_24dp)
            }
        }

        holder.view.image_axis.setImageDrawable(optionImage)
        holder.view.text_graph_option.text = optionTitle
        holder.view.setOnClickListener {
            onClickListener?.onItemClick(list[position])
        }
        holder.view.check_selected_option.isChecked = list[position].isChecked
        holder.view.check_selected_option.setOnClickListener {
            onClickListener?.onCheckBoxClick(list[position].seriesId, (it as CheckBox).isChecked)
        }

    }

    override fun getItemCount(): Int {
        return 4
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun notifyNeData(list: List<SeriesContainer>) {
        this.list = list
        notifyDataSetChanged()
    }
}