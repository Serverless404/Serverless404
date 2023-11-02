package com.example.serverless404

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.res.integerArrayResource
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serverless404.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.internal.ViewUtils.dpToPx
import java.util.Timer
import kotlin.concurrent.schedule

class ReservationApplyActivity: AppCompatActivity() {

    val whoList = ArrayList<WhoItem>()
    var participantList = ArrayList<String>()
    var selectedPosition: Int= 0;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_reservation_apply)

        // 메인화면에서 넘어온 스케쥴 데이터 받기
        var scheduleData = intent.getSerializableExtra("scheduleData") as Schedule
        var actionType = intent.getSerializableExtra("actionType") as String // 생성, 수정 단계 구분
        // 넘어 온 데이터 기준 참가자 세팅
        participantList = scheduleData.participants

        Log.d("scheduleData",scheduleData.toString())

        val recycler_view_who = findViewById<RecyclerView>(R.id.recycler_view_who)
        val recycler_view_participant = findViewById<RecyclerView>(R.id.recycler_view_participants)

        // 사람 검색 목록 추가
        whoList.add(WhoItem("김태현", "정보기획파트", isSelected = false))
        whoList.add(WhoItem("김태성", "인프라파트", isSelected = false))
        whoList.add(WhoItem("김태진", "CLOUD파트", isSelected = false))
        whoList.add(WhoItem("김태호", "시스템운영파트", isSelected = false))
        whoList.add(WhoItem("김태수", "PROCESS파트", isSelected = false))
        whoList.add(WhoItem("김동욱", "정보기획파트", isSelected = false))
        whoList.add(WhoItem("김동수", "PROCESS파트", isSelected = false))
        whoList.add(WhoItem("조용호", "정보기획파트", isSelected = false))
        whoList.add(WhoItem("조용진", "CLOUD파트", isSelected = false))
        whoList.add(WhoItem("조용현", "홈페이지운영파트", isSelected = false))
        whoList.add(WhoItem("조용수", "시스템운영파트", isSelected = false))
        whoList.add(WhoItem("조용히", "AI&PROCESS파트", isSelected = false))

        // 사람 목록 세팅
        var linearManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var whoAdapter = WhoAdapter(whoList)
        recycler_view_who.adapter = whoAdapter
        recycler_view_who.layoutManager = linearManager

        whoAdapter.notifyDataSetChanged()

        // 참가자 목록 세팅
        var gridManager = GridLayoutManager(this, 3)
        var participantAdapter = ParticipantAdapter(participantList)
        participantAdapter.notifyDataSetChanged()

        recycler_view_participant.adapter = participantAdapter
        recycler_view_participant.layoutManager = gridManager

        // 기타 컴포넌트 세팅
        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val popBottomBtn:TextView = findViewById(R.id.popBottomBtn)
        val editTextWho: EditText = findViewById(R.id.edit_who)
        val backBtn: Button = findViewById(R.id.backBtn)
        val addBtn: Button = findViewById(R.id.participant_add_btn)
        val nextBtn: Button = findViewById(R.id.nextBtn)
        val bottomSheetWho: View = findViewById(R.id.bottom_sheet_who)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetWho)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

        // Navigagte
        fun moveToAnotherPage(){
            val intent = Intent(this, ReservationApplyWhereActivity::class.java)
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType);
            startActivity(intent)
        }

//        nextBtn.setOnClickListener {
//            moveToAnotherPage()
//        }

        // 리스트 필터링을 여기서 해주고 리스트만 넘겨주면 띄워주도록 하는편이 좋아보임..
        // 1. 리스트를 만들어 준 다음 새로운 어댑터 생성하고
        // 2. setAdapter(새 어댑터) 해준다.
        editTextWho.setOnEditorActionListener { textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 포커스 제거 => 키보드 내려짐
                editTextWho.clearFocus()

                // 리스트 변경
                var (newList, listLength) = filterList(editTextWho.getText().toString(), whoList);
                whoAdapter = WhoAdapter(newList);
                recycler_view_who.adapter = whoAdapter
                whoAdapter.notifyDataSetChanged()

                // 레이아웃 높이 설정
                if (listLength > 5) {
                    listLength = 5
                }

                var targetHeight = "${listLength * 70 + 34}f".toFloat()
                bottomSheetWho.layoutParams.height = convertDpToPixelsTo(targetHeight, this);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                editTextWho.text = null

                handled = true
            }

            handled
        }

        editTextWho.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                bottomSheetWho.layoutParams.height = convertDpToPixelsTo(32f, this);
                addBtn.text = "참가자 추가"
            } else {
                // 키보드 내리기
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editTextWho.windowToken, 0)
            }
        }

        // 참가자 추가
        addBtn.setOnClickListener {
            if (addBtn.text == "참가자 등록 완료") {
                moveToAnotherPage()
            } else {
                val participantName:String = whoAdapter.getSelectedItem()
                participantList.add(participantName)
                bottomSheetWho.layoutParams.height = convertDpToPixelsTo(32f, this);
                addBtn.text = "참가자 등록 완료"

                participantAdapter = ParticipantAdapter(participantList)
                recycler_view_participant.adapter = participantAdapter
                participantAdapter.notifyDataSetChanged()
            }

        }
    }

    // 유틸 함수
    fun convertDpToPixelsTo(dp: Float, context: Context): Int {
        val metrics = context.resources.displayMetrics;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }

    fun filterList(filterString: String, rawList: ArrayList<WhoItem>) : Pair<ArrayList<WhoItem>, Int> {
        val results = ArrayList<WhoItem>()
        if (filterString.trim { it <= ' '}.isEmpty()) {
            results.addAll(rawList)
            return Pair(results, results.size)

        } else {
            // 이름으로만 검색
            for (item in rawList) {
                if (item.name.contains(filterString)) {
                    results.add(item)
                }
            }
        }

        return Pair(results, results.size)
    }
}