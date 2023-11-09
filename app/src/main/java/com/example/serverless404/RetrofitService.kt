package com.example.serverless404

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface RetrofitService {

    @GET("loginAPI")
    fun login(@Header("input_json") req: String) : Call<ResponseBody>

    @GET("getScheduleListAPI")
    fun getScheduleListAPI(@Header("input_json") req: String) : Call<List<ScheduleDto>>

    @GET("findScheduleAPI")
    fun findScheduleAPI(@Header("input_json") req: String) : Call<List<AvailableTimeDto>>

//    @GET("createScheduleAPI")
//    fun createScheduleAPI(@Header("input_json") req: String) : Call<ResponseBody>

    @GET
    fun createScheduleAPI(@Header("input_json") req: String, @Url url: String) : Call<ResponseBody>

    @GET
    fun editDeleteScheduleAPI(@Header("input_json") req: String, @Url url: String) : Call<ResponseBody>

    @GET("deleteScheduleAPI")
    fun deleteScheduleAPI(@Header("input_json") req: String) : Call<ResponseBody>

    @GET("getUserListAPI")
    fun getUserListAPI(@Header("input_json") req: String) : Call<List<User>>
}