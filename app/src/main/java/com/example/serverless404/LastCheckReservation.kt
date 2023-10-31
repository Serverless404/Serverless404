package com.example.serverless404

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LastCheckReservation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_check_reservation)

        val finishBtn = findViewById<Button>(R.id.check_finish_btn)

        fun moveToAnotherPage(){
            val intent = Intent(this, CalandarMainActivity::class.java)
            startActivity(intent)
        }

        finishBtn.setOnClickListener {
            moveToAnotherPage()
        }
    }
}