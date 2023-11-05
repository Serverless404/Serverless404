package com.example.serverless404

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.res.integerArrayResource
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serverless404.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.internal.ViewUtils.dpToPx
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        if (actionType == "modify") {
            participantList = scheduleData.participants
        } else {
            participantList.add(scheduleData.owner)
        }

        Log.d("scheduleData",scheduleData.toString())

        val recycler_view_who = findViewById<RecyclerView>(R.id.recycler_view_who)
        val recycler_view_participant = findViewById<RecyclerView>(R.id.recycler_view_participants)
        val next_step_btn_container = findViewById<LinearLayout>(R.id.next_step_btn_container)

        var linearManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var whoAdapter = WhoAdapter(whoList)

        // ------- 레트로핏 요청 -------
        // 레트로핏 세팅
        val retrofit = Retrofit.Builder().baseUrl("https://kobvd40ph2.execute-api.ap-northeast-2.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        // 레트로핏 input_json 세팅
        val encodedName = Base64.encodeToString("".toByteArray(), Base64.NO_WRAP) // 전체목록검ㅁㅇㅇ
        var inputJsonString = "{\"name\":\"$encodedName\"}"

        Log.d("회원 목록 조회", "input값 체크: $inputJsonString")

        //레트로핏 HTTP 데이터 요청
        retrofitService.getUserListAPI(inputJsonString)?.enqueue(object :
            Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성공된 경우
                    val resultList: List<User> = response.body() ?: arrayListOf()
                    resultList.forEach{item ->
                        whoList.add(WhoItem(item.name, item.part, isSelected = false))
                    }

                    Log.d("회원리스트 api 성공", "onResponse 성공: $whoList")

                    // 사람 목록 세팅
                    whoAdapter = WhoAdapter(whoList)
                    recycler_view_who.adapter = whoAdapter
                    recycler_view_who.layoutManager = linearManager

                    whoAdapter.notifyDataSetChanged()
                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("가능 시간 조회", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("가능 시간 조회", "onFailure 에러: " + t.message.toString());
            }
        })

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
            scheduleData.participants = participantList
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
                var (newList, listLength) = filterList(editTextWho.getText().toString());
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
                next_step_btn_container.visibility = View.VISIBLE

                handled = true
            }

            handled
        }

        editTextWho.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                bottomSheetWho.layoutParams.height = convertDpToPixelsTo(32f, this);
                addBtn.text = "참가자 추가"
                next_step_btn_container.visibility = View.GONE
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
                if (participantName == "") {
                    Toast.makeText(this@ReservationApplyActivity, "선택된 회원이 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    participantList.add(participantName)
                    bottomSheetWho.layoutParams.height = convertDpToPixelsTo(32f, this);
                    addBtn.text = "참가자 등록 완료"

                    participantAdapter = ParticipantAdapter(participantList)
                    recycler_view_participant.adapter = participantAdapter
                    participantAdapter.notifyDataSetChanged()
                }
            }

        }
    }

    // 유틸 함수
    fun convertDpToPixelsTo(dp: Float, context: Context): Int {
        val metrics = context.resources.displayMetrics;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }

    fun filterList(filterString: String) : Pair<ArrayList<WhoItem>, Int> {
        val results = ArrayList<WhoItem>()
        if (filterString.trim { it <= ' '}.isEmpty()) {
            results.addAll(whoList)
            return Pair(results, results.size)

        } else {
            // 이름으로만 검색
            for (item in whoList) {
                if (item.name.contains(filterString)) {
                    results.add(item)
                }
            }
        }

        return Pair(results, results.size)
    }
}