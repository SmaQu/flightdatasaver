package com.smaqu.rcgraphsystem.adapter

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import kotlinx.android.synthetic.main.item_device.view.*

class PairedDevicesAdapter(private var list: MutableList<BluetoothDevice>) : RecyclerView.Adapter<PairedDevicesAdapter.ViewHolder>() {

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    interface ClickListener {
        fun onItemClick(bluetoothDevice: BluetoothDevice)
    }

    private var clickListener: ClickListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tv_device_name?.text = list[position].name
        holder.view.setOnClickListener {
            clickListener?.onItemClick(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    fun updateAdapterItems(newList: Set<BluetoothDevice>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearAdapterItems() {
        list.clear()
        notifyDataSetChanged()
    }
}