package com.example.serverless404

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Schedule(
    @SerializedName("owner")
    val owner: String = "",
    @SerializedName("schedule_id")
    var scheduleId: String = "",
    @SerializedName("year")
    var year: String = "",
    @SerializedName("month")
    var month: String = "",
    @SerializedName("date")
    var date: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("detail")
    var detail: String = "",
    @SerializedName("start_time")
    var startTime: String = "",
    @SerializedName("end_time")
    var endTime: String = "",
    @SerializedName("participants")
    var participants: ArrayList<String> = arrayListOf<String>(),
    @SerializedName("place")
    var place: String = ""
) : Serializable

