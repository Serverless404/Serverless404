package com.example.serverless404

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LastCheckReservation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_check_reservation)

        // 메인화면에서 넘어온 스케쥴 데이터 받기
        var scheduleData = intent.getSerializableExtra("scheduleData") as Schedule

        Log.d("scheduleData: ", "넘어온 데이터는..${scheduleData}")
        var actionType = intent.getSerializableExtra("actionType") as String // 생성, 수정 단계 구분

        val finishBtn = findViewById<Button>(R.id.check_finish_btn)
        val backBtn = findViewById<Button>(R.id.backBtn)
        val participantText = findViewById<TextView>(R.id.participants_text)
        val placeText = findViewById<TextView>(R.id.place_text)
        val dateText = findViewById<TextView>(R.id.date_text)
        val detailText = findViewById<TextView>(R.id.detail_text)

        val editTitle = findViewById<EditText>(R.id.edit_title)
        val editContent = findViewById<EditText>(R.id.edit_content) // 처음에 gone
        val guideText = findViewById<TextView>(R.id.guide_text)
        val titleText = findViewById<TextView>(R.id.title_text) // 처음에 gone

        if (actionType == "edit" && scheduleData.title != "" && scheduleData.detail != "") {
            editTitle.setText(scheduleData.title)
            editContent.setText(scheduleData.detail)
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, ReservationApplyCalculateActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
            finish()
        }

        // api 첫 성공 플래그
        var isSuccess = false;

        // 데이터 세팅
        participantText.text = scheduleData.participants.joinToString(",")
        placeText.text = scheduleData.place
        dateText.text = "${scheduleData.date} / ${scheduleData.startTime} ~ ${scheduleData.endTime}"

        if (scheduleData.scheduleId == "") {
            finishBtn.isEnabled = false
            finishBtn.setBackgroundResource(R.drawable.item_btn_16_corners_disabled);
        }

        editTitle.setOnEditorActionListener { textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 포커스 제거 => 키보드 내려짐
                editTitle.clearFocus()

                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editTitle.windowToken, 0)

                scheduleData.title = editTitle.getText().toString()
                titleText.text = scheduleData.title
                guideText.text = "회의의 내용을 적어주세요"

                titleText.visibility = View.VISIBLE
                editTitle.visibility = View.GONE
                editContent.visibility = View.VISIBLE

                finishBtn.isEnabled = true
                finishBtn.setBackgroundResource(R.drawable.item_btn_16_corners_enabled);
                handled = true
            }

            handled
        }

        editContent.setOnEditorActionListener { textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 포커스 제거 => 키보드 내려짐
                editContent.clearFocus()

                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editContent.windowToken, 0)

                scheduleData.detail = editContent.getText().toString()
                detailText.text = scheduleData.detail

                editContent.visibility = View.GONE
                guideText.visibility = View.GONE

                handled = true
            }

            handled
        }

        val creatUrl = "https://lbc97qf2hj.execute-api.ap-northeast-2.amazonaws.com/createScheduleAPI"
        val deleteUrl = "https://6kerrjpzcj.execute-api.ap-northeast-2.amazonaws.com/deleteScheduleAPI"

        // 스케줄 생성 요청
        // 레트로핏 세팅
        val retrofit = Retrofit.Builder().baseUrl("https://6kerrjpzcj.execute-api.ap-northeast-2.amazonaws.com/").addConverterFactory(GsonConverterFactory.create()).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        fun editDeleteSchedule() {
            val owner = Base64.encodeToString(scheduleData.owner.toByteArray(), Base64.NO_WRAP);

            var inputJsonString =
                "{\"owner\":\"$owner\",\"schedule_id\":\"${scheduleData.scheduleId}\"}"

            retrofitService.editDeleteScheduleAPI(inputJsonString, deleteUrl)?.enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val resultString: String = response.body()?.string() ?: ""
                        Log.d("수정 전 일정 삭제", "onResponse 성공: $resultString")
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("수정 전 일정 삭제", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("수정 전 일정 삭제", "onFailure 에러: " + t.message.toString());
                }
            })
        }

        fun callCreateSchedule() {
            // 레트로핏 input_json 세팅
            if (scheduleData.scheduleId == "") { // 처음 만들때만
                scheduleData.scheduleId = "${scheduleData.date}${scheduleData.startTime.replace(":", "")}"
                scheduleData.year = "${scheduleData.date.substring(0 until 4)}"
                scheduleData.month = "${scheduleData.date.substring(4 until 6)}"
                scheduleData.date = scheduleData.date.substring(6 until 8)
            } else {
                scheduleData.scheduleId = "${scheduleData.year}${scheduleData.month}${scheduleData.date}${scheduleData.startTime.replace(":", "")}"
            }


            // 요청 보낼 데이터 세팅 인코딩
            val participants = Base64.encodeToString(scheduleData.participants.joinToString(",").toByteArray(), Base64.NO_WRAP)
            val title = Base64.encodeToString(scheduleData.title.toByteArray(), Base64.NO_WRAP)
            val detail = Base64.encodeToString(scheduleData.detail.toByteArray(), Base64.NO_WRAP)
            val place = Base64.encodeToString(scheduleData.place.toByteArray(), Base64.NO_WRAP)

            scheduleData.participants.forEach {
                val owner = Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP)

                var inputJsonString = "{\"owner\":\"$owner\",\"schedule_id\":\"${scheduleData.scheduleId}\",\"date\":\"${scheduleData.date}\",\"detail\":\"$detail\",\"end_time\":\"${scheduleData.endTime}\",\"month\":\"${scheduleData.month}\",\"participants\":\"$participants\",\"place\":\"$place\",\"start_time\":\"${scheduleData.startTime}\",\"title\":\"$title\",\"year\":\"${scheduleData.year}\"}"

                retrofitService.createScheduleAPI(inputJsonString, creatUrl)?.enqueue(object :
                    Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if(response.isSuccessful){
                            // 정상적으로 통신이 성공된 경우
                            val resultString: String = response.body()?.string() ?: ""
                            Log.d("일정 선택 api 성공", "onResponse 성공: $resultString")

                            if (resultString.contains("SUCCESS")) {
                                if (isSuccess == false) {
                                    isSuccess = true
                                    Log.d("첫 번째 성공이니 detail로 이동시켜라", "onResponse 성공: $resultString")
                                } else {
                                    Log.d("첫 번째 성공아니니 아무것도 안 해도 된다.", "onResponse 성공: $resultString")
                                }
                            }
                        }else{
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("가능 시간 조회", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("가능 시간 조회", "onFailure 에러: " + t.message.toString());
                    }
                })
            }
        }


        fun moveToAnotherPage(){
            val intent = Intent(this, DetailScheduleActivity::class.java)

            intent.putExtra("scheduleData", scheduleData)
            Log.d("scheduleData Intent", scheduleData.toString());
            intent.putExtra("actionType", actionType);
            startActivity(intent)
        }

        finishBtn.setOnClickListener {
            if (scheduleData.detail == "") {
                Toast.makeText(this@LastCheckReservation, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if (actionType == "edit" && scheduleData.scheduleId != "") {
                    editDeleteSchedule()
                    callCreateSchedule()
                    moveToAnotherPage()
                } else { // 새로 생성
                    callCreateSchedule()
                    moveToAnotherPage()
                }
            }
        }
    }
}