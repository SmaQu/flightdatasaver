package com.smaqu.rcgraphsystem.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.models.FileEntity
import kotlinx.android.synthetic.main.item_file.view.*

class FileListAdapter(private var list: List<FileEntity>) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private var clickListener: ClickListener? = null
    private var longClickListener: LongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tv_file_name?.text = list[position].name
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

    fun setOnLongClickListener(longClickListener: LongClickListener) {
        this.longClickListener = longClickListener
    }

    fun updateAdapter(newList: List<FileEntity>) {
        this.list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    interface ClickListener {
        fun onItemClick(fileEntity: FileEntity)
    }

    interface LongClickListener {
        fun onLongItemClick(fileEntity: FileEntity): Boolean
    }
}
