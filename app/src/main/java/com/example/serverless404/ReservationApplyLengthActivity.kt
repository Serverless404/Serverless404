package com.example.serverless404

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener

class ReservationApplyLengthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_apply_length)

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

        timeSlider.addOnChangeListener { slider, value, fromUser ->
            minuteText.text = "${value.toInt()}분"
        }

        fun moveToAnotherPage(){
            val intent = Intent(this, ReservationApplyCalculateActivity::class.java)
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