package com.example.serverless404

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WhereAdapter(val itemList: ArrayList<String>) :
    RecyclerView.Adapter<WhereAdapter.WhereViewHolder>() {

    private var selectedItemPosition = -1
    private var selectedLayout: TextView? = null;

    var whereList = ArrayList<String>()

    init {
        whereList.addAll(itemList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhereViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_where, parent, false)
        return WhereViewHolder(view)
    }

    override fun onBindViewHolder(holder: WhereViewHolder, position: Int) {
        val wItem = holder.itemView.findViewById<TextView>(R.id.where_data)
        val text = whereList[position]

        // Item Initialize
        if (position == selectedItemPosition) {
            wItem.setBackgroundResource(R.drawable.item_bottom_item_shape_clicked)
        } else {
            wItem.setBackgroundResource(R.drawable.item_bottom_item_shape_unclicked)
        }

        // Item ClickListener
        wItem.setOnClickListener {
            val currentPosition = holder.adapterPosition

            if (selectedItemPosition == currentPosition) {

                selectedItemPosition = -1
                wItem.setBackgroundResource(R.drawable.item_bottom_item_shape_unclicked)
                selectedLayout = null
            } else {

                if (selectedItemPosition >= 0 || selectedLayout != null) {
                    selectedLayout?.setBackgroundResource(R.drawable.item_bottom_item_shape_unclicked)
                }

                selectedItemPosition = currentPosition
                selectedLayout = wItem
                wItem.setBackgroundResource(R.drawable.item_bottom_item_shape_clicked)
            }
        }

        holder.updateViewHolder(text)
    }

    override fun getItemCount(): Int {
        return whereList.size
    }

    fun selectedItem(): String {
        return whereList[selectedItemPosition]
    }

    // 뷰 홀더
    inner class WhereViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateViewHolder(text: String) {
            val where_data = itemView.findViewById<TextView>(R.id.where_data)
            where_data.text = text
        }
    }
}