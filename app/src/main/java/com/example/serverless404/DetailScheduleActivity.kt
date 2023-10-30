package com.example.serverless404

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailScheduleActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_schedule)

        // 메인화면에서 넘어온 스케쥴 데이터 받기
        val detailSchedule = intent.getSerializableExtra("data") as Schedule
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

        // 툴바의 홈가기 버튼 클릭 처리
        firstPageIcon.setOnClickListener {
            val intent = Intent(this@DetailScheduleActivity,CalandarMainActivity::class.java)
//            intent.putExtra("data",detailSchedule)
            startActivity(intent)
            finish()
        }

    }
}

