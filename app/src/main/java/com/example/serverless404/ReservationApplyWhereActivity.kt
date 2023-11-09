package com.example.serverless404

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ReservationApplyWhereActivity : AppCompatActivity() {

    val whereOutList = ArrayList<String>()
    val whereMainList = ArrayList<String>()
    val whereYoungList = ArrayList<String>()
    val initList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_apply_where)

        // 메인화면에서 넘어온 스케쥴 데이터 받기
        var scheduleData = intent.getSerializableExtra("scheduleData") as Schedule
        var actionType = intent.getSerializableExtra("actionType") as String // 생성, 수정 단계 구분
        Log.d("scheduleData",scheduleData.toString())

        val recycler_view_where = findViewById<RecyclerView>(R.id.recycler_view_where)

        // 목록 만들어 놓기
        whereOutList.add("멀티캠퍼스")
        whereOutList.add("패스트캠퍼스")
        whereOutList.add("위워크")
        whereOutList.add("토즈")
        whereOutList.add("스타벅스")
        whereOutList.add("투썸플레이스")
        whereOutList.add("폴바셋")

        whereMainList.add("2층 / 2-4")
        whereMainList.add("3층 / 3-1")
        whereMainList.add("4층 / 4-2")
        whereMainList.add("25층 / 25-1")
        whereMainList.add("26층 / 26-1")
        whereMainList.add("27층 / 27-4")
        whereMainList.add("28층 / 28-2")
        whereMainList.add("31층 / 31-1")

        whereYoungList.add("2층 / 2-5")
        whereYoungList.add("8층 / 8-1")
        whereYoungList.add("8층 / 8-2")
        whereYoungList.add("8층 / 8-3")
        whereYoungList.add("7층 / 7-2")
        whereYoungList.add("7층 / 7-1")

        // 세팅
        var linearManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var whereAdapter = WhereAdapter(initList)
        recycler_view_where.adapter = whereAdapter
        recycler_view_where.layoutManager = linearManager

        whereAdapter.notifyDataSetChanged()

        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val popBottomBtn: TextView = findViewById(R.id.popBottomBtn)
        val backBtn: Button = findViewById(R.id.backBtn)
        val bottomSheetWhere: View = findViewById(R.id.bottom_sheet_where)
        val outBtn: LinearLayout = findViewById(R.id.out_place_btn)
        val mainBtn: LinearLayout = findViewById(R.id.main_place_btn)
        val youngBtn: LinearLayout = findViewById(R.id.young_place_btn)
        val finishBtn: Button = findViewById(R.id.where_finish_btn)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetWhere)

        backBtn.setOnClickListener {
            val intent = Intent(this, ReservationApplyActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
            finish()
        }

        // 버튼 누를 때 마다 리스트 변경
        outBtn.setOnClickListener{
            whereAdapter = WhereAdapter(whereOutList)
            recycler_view_where.adapter = whereAdapter

            whereAdapter.notifyDataSetChanged()
            outBtn.setBackgroundResource(R.drawable.item_btn_16_corners);
            mainBtn.setBackgroundResource(R.drawable.item_btn_16_corners_white);
            youngBtn.setBackgroundResource(R.drawable.item_btn_16_corners_white);

            var targetHeight = "384f".toFloat()
            bottomSheetWhere.layoutParams.height = convertDpToPixelsTo(targetHeight, this);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        youngBtn.setOnClickListener{
            whereAdapter = WhereAdapter(whereYoungList)
            recycler_view_where.adapter = whereAdapter

            whereAdapter.notifyDataSetChanged()
            outBtn.setBackgroundResource(R.drawable.item_btn_16_corners_white);
            mainBtn.setBackgroundResource(R.drawable.item_btn_16_corners_white);
            youngBtn.setBackgroundResource(R.drawable.item_btn_16_corners);

            var targetHeight = "384f".toFloat()
            bottomSheetWhere.layoutParams.height = convertDpToPixelsTo(targetHeight, this);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        mainBtn.setOnClickListener{
            whereAdapter = WhereAdapter(whereMainList)
            recycler_view_where.adapter = whereAdapter

            whereAdapter.notifyDataSetChanged()
            outBtn.setBackgroundResource(R.drawable.item_btn_16_corners_white);
            mainBtn.setBackgroundResource(R.drawable.item_btn_16_corners);
            youngBtn.setBackgroundResource(R.drawable.item_btn_16_corners_white);

            var targetHeight = "384f".toFloat()
            bottomSheetWhere.layoutParams.height = convertDpToPixelsTo(targetHeight, this);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        fun moveToAnotherPage(){
            val intent = Intent(this, ReservationApplyLengthActivity::class.java)
            scheduleData.place = whereAdapter.selectedItem()
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
        }

//        popBottomBtn.setOnClickListener {
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//        }

        finishBtn.setOnClickListener {
            moveToAnotherPage()
        }

    }

    // 유틸 함수
    fun convertDpToPixelsTo(dp: Float, context: Context): Int {
        val metrics = context.resources.displayMetrics;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }
}