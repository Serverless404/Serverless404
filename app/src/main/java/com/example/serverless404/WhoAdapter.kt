package com.example.serverless404

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.serverless404.databinding.ActivityMainBinding
import com.example.serverless404.databinding.ActivtyReservationApplyBinding
import kotlinx.coroutines.selects.select

class WhoAdapter(
    val itemList: ArrayList<WhoItem>
) : RecyclerView.Adapter<WhoAdapter.WhoViewHolder>() {

    private var selectedItemPosition = -1
    private var selectedLayout: TextView? = null;

    var filteredWhoList = ArrayList<WhoItem>()

    init {
        filteredWhoList.addAll(itemList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_who, parent, false)
        return WhoViewHolder(view)
    }

    override fun onBindViewHolder(holder: WhoViewHolder, position: Int) {
        val wItem = holder.itemView.findViewById<TextView>(R.id.who_data)
        val text = filteredWhoList[position].name + " / " + filteredWhoList[position].team

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
                wItem.isSelected = false
                wItem.setBackgroundResource(R.drawable.item_bottom_item_shape_unclicked)
                selectedLayout = null
            } else {

                if (selectedItemPosition >= 0 || selectedLayout != null) {
                    selectedLayout?.setBackgroundResource(R.drawable.item_bottom_item_shape_unclicked)
                }

                selectedItemPosition = currentPosition
                wItem.isSelected = true
                selectedLayout = wItem
                wItem.setBackgroundResource(R.drawable.item_bottom_item_shape_clicked)
            }
        }

        holder.updateViewHolder(text)
    }

    override fun getItemCount(): Int {
        return filteredWhoList.count()
    }

    fun getSelectedItem(): String {
        return if (selectedItemPosition >= 0)
            filteredWhoList[selectedItemPosition].name
        else ""
    }

    // 뷰 홀더
    inner class WhoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateViewHolder(text: String) {
            val who_data = itemView.findViewById<TextView>(R.id.who_data)
            who_data.text = text
        }
    }
}