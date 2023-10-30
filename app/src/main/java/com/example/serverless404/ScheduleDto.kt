package com.example.serverless404

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ScheduleDto(
    @SerializedName("owner")
    val owner: String,
    @SerializedName("schedule_id")
    val scheduleId: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("month")
    val month: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("detail")
    val detail: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("participants")
    val participants: String,
    @SerializedName("place")
    val place: String
) : Serializable

