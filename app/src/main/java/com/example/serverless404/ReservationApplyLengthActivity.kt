package com.example.serverless404

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import kotlin.math.min

class ReservationApplyLengthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_apply_length)

        var scheduleData = intent.getSerializableExtra("scheduleData") as Schedule
        var actionType = intent.getSerializableExtra("actionType") as String // 생성, 수정 단계 구분

        Log.d("scheduleData",scheduleData.toString())

        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val popBottomBtn: TextView = findViewById(R.id.popBottomBtn)
        val backBtn: Button = findViewById(R.id.backBtn)
        val nextBtn: Button = findViewById(R.id.nextBtn)
        val bottomSheetLength: View = findViewById(R.id.bottom_sheet_length)
        val timeSlider: Slider = findViewById(R.id.time_slider)
        val minuteText: TextView = findViewById(R.id.minute_text)
        val lengthFinishBtn: Button = findViewById(R.id.length_finish_btn)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLength)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

        minuteText.text = "90분"

        timeSlider.addOnChangeListener { slider, value, fromUser ->
            minuteText.text = "${value.toInt()}분"
        }

        fun moveToAnotherPage(){
            val intent = Intent(this, ReservationApplyCalculateActivity::class.java)
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
        }

//        nextBtn.setOnClickListener {
//            moveToAnotherPage()
//        }

        lengthFinishBtn.setOnClickListener {
            moveToAnotherPage()
        }
    }
}