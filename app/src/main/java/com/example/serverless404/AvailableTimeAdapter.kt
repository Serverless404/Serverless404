package com.example.serverless404

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AvailableTimeAdapter(val availableTimeList: ArrayList<AvailableTimeDto>) :
    RecyclerView.Adapter<AvailableTimeAdapter.TimeViewHolder>() {

    private var selectedItemPosition = -1
    private var selectedLayout: TextView? = null;

    var timeList = ArrayList<AvailableTimeDto>()

    init {
        timeList.addAll(availableTimeList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_time, parent, false)
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val wItem = holder.itemView.findViewById<TextView>(R.id.time_data)
        val text = "${timeList[position].date} / ${timeList[position].startTime} ~ ${timeList[position].endTime}"

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
        return timeList.size
    }

    // 뷰 홀더
    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateViewHolder(text: String) {
            val timd_data = itemView.findViewById<TextView>(R.id.time_data)
            timd_data.text = text
        }
    }
}