package com.example.serverless404

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReservationApplyCalculateActivity : AppCompatActivity() {

    val initList = ArrayList<AvailableTimeDto>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_apply_calculate)

        // 메인화면에서 넘어온 스케쥴 데이터 받기
        var scheduleData = intent.getSerializableExtra("scheduleData") as Schedule
        var actionType = intent.getSerializableExtra("actionType") as String // 생성, 수정 단계 구분

        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val secondBottomSheetBehavior: BottomSheetBehavior<*>?
        val popBottomBtn: TextView = findViewById(R.id.popBottomBtn)
        val backBtn: Button = findViewById(R.id.backBtn)
        val nextBtn: Button = findViewById(R.id.nextBtn)
        val bottomSheetCalculate: View = findViewById(R.id.bottom_sheet_calculate)
        val bottomSheetTimeSelect: View = findViewById(R.id.bottom_sheet_time_select)
        val randomTimeText: TextView = findViewById(R.id.random_time_text)
        val recycler_view_time = findViewById<RecyclerView>(R.id.recycler_view_time)
        val nextStepContainer = findViewById<LinearLayout>(R.id.next_step_btn_container)
        val finishBtn = findViewById<Button>(R.id.time_finish_btn)
        val guideText = findViewById<TextView>(R.id.guide_text)

        var linearManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var timeAdapter = AvailableTimeAdapter(initList)
        recycler_view_time.adapter = timeAdapter
        recycler_view_time.layoutManager = linearManager

        timeAdapter.notifyDataSetChanged()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCalculate)
        secondBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetTimeSelect)

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

        var targetHeight = "524f".toFloat()
        bottomSheetTimeSelect.layoutParams.height = convertDpToPixelsTo(targetHeight, this);

        // 2초 간 계산하는 화면 보여줌
        val timer = object : CountDownTimer(4000, 500) {
            var index = 0
            var timeArray = arrayOf("14 : 00", "12 : 23", "02 : 32", "05 : 11", "07 : 22", "23 : 56"
            ,"09 : 00", "22 : 30", "12 : 00", "11 : 10", "19 : 20")

            override fun onTick(p0: Long) {
                index += 1
                Log.d("onTick: ", "이번 틱은..${timeArray[index]}")
                randomTimeText.setText(timeArray[index])
            }

            override fun onFinish() {
                // 1. 기존 bottomsheet 안 보이게 하고
                bottomSheetCalculate.visibility = View.GONE
                guideText.text = "가능한 시간은 다음과 같네요"
                // 2. 시간 목록 시트 보이게 변경
                bottomSheetTimeSelect.visibility = View.VISIBLE
                nextStepContainer.visibility = View.VISIBLE

//                secondBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                // timer 파괴
                cancel()
                Log.d("onFinish: ", "끝났어요")
            }
        }.start()

        // 레트로핏 세팅
        val retrofit = Retrofit.Builder().baseUrl("https://06nyju3pp7.execute-api.ap-northeast-2.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        // 레트로핏 input_json 세팅
        val participants = scheduleData.participants.joinToString(",")
        Log.d("가능 시간 조회", "참가자 값 체크: $participants")
        val encodedParticipant = Base64.encodeToString(participants.toByteArray(), Base64.NO_WRAP)
        var inputJsonString = "{\"participants\":\"$encodedParticipant\",\"require_time\":\"90\"}"

        Log.d("가능 시간 조회", "input값 체크: $inputJsonString")

        //레트로핏 HTTP 데이터 요청
        retrofitService.findScheduleAPI(inputJsonString)?.enqueue(object :
            Callback<List<AvailableTimeDto>> {

            override fun onResponse(call: Call<List<AvailableTimeDto>>, response: Response<List<AvailableTimeDto>>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성공된 경우
                    val resultList: List<AvailableTimeDto> = response.body() ?: arrayListOf()

                    val availableTimeList = resultList.toTypedArray().toCollection(ArrayList<AvailableTimeDto>())

                    timeAdapter = AvailableTimeAdapter(availableTimeList)
                    recycler_view_time.adapter = timeAdapter
                    timeAdapter.notifyDataSetChanged()

                    secondBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

                    Log.d("가능 시간 조회", "onResponse 성공: $availableTimeList")

                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("가능 시간 조회", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<List<AvailableTimeDto>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("가능 시간 조회", "onFailure 에러: " + t.message.toString());
            }
        })

        fun moveToAnotherPage(){
            val intent = Intent(this, LastCheckReservation::class.java)
            val selectedItem = timeAdapter.selectedItem()
            scheduleData.date = selectedItem.date
            scheduleData.startTime = selectedItem.startTime
            scheduleData.endTime = selectedItem.endTime

//            scheduleData.date = "20231130"
//            scheduleData.startTime = "09:30"
//            scheduleData.endTime = "11:30"

            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
        }

        finishBtn.setOnClickListener {
            moveToAnotherPage()
        }
    }

    fun convertDpToPixelsTo(dp: Float, context: Context): Int {
        val metrics = context.resources.displayMetrics;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }
}