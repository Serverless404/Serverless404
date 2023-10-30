package com.example.serverless404

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {

    @GET("loginAPI")
    fun login(@Header("input_json") req: String) : Call<ResponseBody>

    @GET("getScheduleListAPI")
    fun getScheduleListAPI(@Header("input_json") req: String) : Call<List<ScheduleDto>>
}