package com.smaqu.rcgraphsystem.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import kotlinx.android.synthetic.main.item_file.view.*

class FileToDownloadAdapter(private var list: List<String>) : RecyclerView.Adapter<FileToDownloadAdapter.ViewHolder>() {

    private var clickListener: ClickListener? = null
    private var longClickListener: LongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tv_file_name?.text = list[position]
        holder.view.setOnClickListener {
            clickListener?.onItemClick(list[position])
        }
        holder.view.setOnLongClickListener {
            longClickListener?.onLongItemClick(list[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    fun updateAdapter(newList: List<String>) {
        this.list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    interface ClickListener {
        fun onItemClick(fileName: String)
    }

    interface LongClickListener {
        fun onLongItemClick(fileName: String): Boolean
    }
}
