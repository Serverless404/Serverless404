package com.example.serverless404

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ReservationApplyLengthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_apply_length)

        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val popBottomBtn: TextView = findViewById(R.id.popBottomBtn)
        val backBtn: Button = findViewById(R.id.backBtn)
        val nextBtn: Button = findViewById(R.id.nextBtn)
        val bottomSheetLength: View = findViewById(R.id.bottom_sheet_length)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLength)

        popBottomBtn.setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }
}