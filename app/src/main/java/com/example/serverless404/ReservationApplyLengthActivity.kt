package com.example.serverless404

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Dimension
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.RangeSlider
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
        val bottomSheetLength: View = findViewById(R.id.bottom_sheet_length)
        val timeSlider: Slider = findViewById(R.id.time_slider)
        val rangeSlider : RangeSlider = findViewById(R.id.range_slider)
        val minuteText: TextView = findViewById(R.id.minute_text)
        val lengthFinishBtn: Button = findViewById(R.id.length_finish_btn)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLength)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

        backBtn.setOnClickListener {
            val intent = Intent(this, ReservationApplyWhereActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
            finish()
        }

        // 회의 생성
        if (actionType == "create") {
            rangeSlider.visibility = View.GONE
            minuteText.text = "90분"
            val intVal = 90
            timeSlider.value = intVal.toFloat()

            timeSlider.addOnChangeListener { slider, value, fromUser ->
                minuteText.text = "${value.toInt()}분"
            }
        }

        // 일반 일정 생성
        if (actionType == "edit") {
            // edit일 경우 초기화 해주기

            //
            timeSlider.visibility = View.GONE
            minuteText.text = "10시 00분 ~ 15시 00분"
            val intSpVal = 32
            minuteText.setTextSize(Dimension.SP, intSpVal.toFloat())

            scheduleData.startTime = "10:00"
            scheduleData.endTime = "15:00"

            rangeSlider.addOnChangeListener { slider, value, fromUser ->
                val startValue = slider.values[0]
                val endValue = slider.values[1]

                scheduleData.startTime ="${
                    (startValue / 60).toInt().toString().padStart(2, '0')
                }:${
                    (startValue % 60).toInt().toString().padStart(2, '0')
                }"
                scheduleData.endTime = "${
                    (endValue / 60).toInt().toString().padStart(2, '0')
                }:${
                    (endValue % 60).toInt().toString().padStart(2, '0')
                }"

                val nextTimeText = "${
                    (startValue / 60).toInt().toString().padStart(2, '0')
                }시 ${
                    (startValue % 60).toInt().toString().padStart(2, '0')
                }분 ~ ${
                    (endValue / 60).toInt().toString().padStart(2, '0')
                }시 ${(endValue % 60).toInt().toString().padStart(2, '0')}분"

                minuteText.setText(nextTimeText)
            }
        }

        fun moveToAnotherPage(){
            if (actionType == "create") {
                val intent = Intent(this, ReservationApplyCalculateActivity::class.java)
                intent.putExtra("scheduleData", scheduleData)
                intent.putExtra("actionType", actionType);
                startActivity(intent)
            }

            if (actionType == "edit") {
                val intent = Intent(this, LastCheckReservation::class.java)
                intent.putExtra("scheduleData", scheduleData)
                intent.putExtra("actionType", actionType);
                startActivity(intent)
            }
        }

//        nextBtn.setOnClickListener {
//            moveToAnotherPage()
//        }

        lengthFinishBtn.setOnClickListener {
            moveToAnotherPage()
        }
    }
}