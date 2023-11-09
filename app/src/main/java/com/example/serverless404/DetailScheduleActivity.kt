package com.example.serverless404

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailScheduleActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_schedule)

        // 메인화면에서 넘어온 스케쥴 데이터 받기
        val detailSchedule = intent.getSerializableExtra("scheduleData") as Schedule
        Log.d("data",detailSchedule.toString())

        val date = findViewById<TextView>(R.id.detail_date)
        val title = findViewById<TextView>(R.id.detail_title)
        val participants = findViewById<TextView>(R.id.detail_participants)
        val detail = findViewById<TextView>(R.id.detail_detail)
        val time = findViewById<TextView>(R.id.detail_time)
        val place = findViewById<TextView>(R.id.detail_place)
        val owner = findViewById<TextView>(R.id.detail_owner)

        val firstPageIcon = findViewById<ImageView>(R.id.firstPageImage)
        val modifyButton = findViewById<Button>(R.id.detail_modifyButton)
        val deleteButton = findViewById<Button>(R.id.detail_deleteButton)
        
        // 각 텍스트 뷰 데이터 바인딩
        date.text = "${detailSchedule.year}년 ${detailSchedule.month}월 ${detailSchedule.date}일"
        title.text = detailSchedule.title
        participants.text = detailSchedule.participants.joinToString(",")
        detail.text = detailSchedule.detail
        time.text = "${detailSchedule.startTime} ~ ${detailSchedule.endTime}"
        place.text = detailSchedule.place
        owner.text = detailSchedule.owner

        // SharedPreferences 에 상세페이지 에서 메인 이동 저장
        val detailToMain : SharedPreferences = getSharedPreferences("detail_to_main", Activity.MODE_PRIVATE)
        val spEditor = detailToMain.edit()
        spEditor.putString("detail_to_main","SUCCESS")
        spEditor.apply()

        // SharedPreferences 에 날짜 저장
        val spYear : SharedPreferences = getSharedPreferences("year", Activity.MODE_PRIVATE)
        val spMonth : SharedPreferences = getSharedPreferences("month", Activity.MODE_PRIVATE)
        val spDay : SharedPreferences = getSharedPreferences("day", Activity.MODE_PRIVATE)

        val spYearEditor = spYear.edit()
        spYearEditor.putString("year",detailSchedule.year)
        spYearEditor.apply()

        val spMonthEditor = spMonth.edit()
        spMonthEditor.putString("month",detailSchedule.month)
        spMonthEditor.apply()

        val spDayEditor = spDay.edit()
        spDayEditor.putString("day",detailSchedule.date)
        spDayEditor.apply()

        // 툴바의 홈가기 버튼 클릭 처리
        firstPageIcon.setOnClickListener {
            val intent = Intent(this@DetailScheduleActivity,CalandarMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // 삭제 레트로핏
        // 삭제 후 홈 화면 이동
        val retrofit = Retrofit.Builder().baseUrl("https://6kerrjpzcj.execute-api.ap-northeast-2.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        fun callDeleteSchedule() {
            // 요청 보낼 데이터 세팅 인코딩
            val owner = Base64.encodeToString(detailSchedule.owner.toByteArray(), Base64.NO_WRAP)
            val scheduleId = detailSchedule.scheduleId
            // 레트로핏 input_json 세팅
            var inputJsonString = "{\"owner\":\"$owner\",\"schedule_id\":\"$scheduleId\"}"

            retrofitService.deleteScheduleAPI(inputJsonString)?.enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            val resultString: String = response.body()?.string() ?: ""
                            Log.d("일정 삭제 api 성공", "onResponse 성공: $resultString")

                            if (resultString.contains("SUCCESS")) {
                                val intent = Intent(this@DetailScheduleActivity,CalandarMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.d("일정 삭제 조회", "onResponse 실패")
                        }
                }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("일정 삭제 조회", "onFailure 에러: " + t.message.toString());
                    }
                }
            )
        }

        deleteButton.setOnClickListener {
            callDeleteSchedule()
        }

        // 수정으로 이동
        fun moveToAnotherPage(){
            val intent = Intent(this, ReservationApplyActivity::class.java)

            intent.putExtra("scheduleData", detailSchedule)
            intent.putExtra("actionType", "edit");
            startActivity(intent)
        }

        modifyButton.setOnClickListener {
            moveToAnotherPage()
        }
    }
}

