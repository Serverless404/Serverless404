package com.example.serverless404

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParticipantAdapter(val itemList: ArrayList<String>) :
    RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {


    var participantList = ArrayList<String>()

    init {
        participantList.addAll(itemList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_participant, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.participant_name.text = participantList[position]
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    // 뷰 홀더
    inner class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participant_name = itemView.findViewById<TextView>(R.id.participant_name)
    }
}