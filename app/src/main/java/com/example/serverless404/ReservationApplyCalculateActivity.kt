package com.example.serverless404

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ReservationApplyCalculateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_apply_calculate)

        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val popBottomBtn: TextView = findViewById(R.id.popBottomBtn)
        val backBtn: Button = findViewById(R.id.backBtn)
        val nextBtn: Button = findViewById(R.id.nextBtn)
        val bottomSheetCalculate: View = findViewById(R.id.bottom_sheet_calculate)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCalculate)

        popBottomBtn.setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        fun moveToAnotherPage(){
            val intent = Intent(this, ReservationApplyTimeSelectActivity::class.java)
            startActivity(intent)
        }

        nextBtn.setOnClickListener {
            moveToAnotherPage()
        }
    }
}