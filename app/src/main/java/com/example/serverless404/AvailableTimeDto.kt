package com.example.serverless404

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AvailableTimeDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
) : Serializable

