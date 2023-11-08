package com.example.serverless404

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.Calendar

class CalandarMainActivity : AppCompatActivity() {

    lateinit var calendar: MaterialCalendarView
    lateinit var recyclerViewAdapter : RecyclerViewAdapter
    lateinit var recyclerView : RecyclerView
    var selectedScheduleArrayList = arrayListOf<Schedule>()
    var totalScheduleArrayList : ArrayList<Schedule> = arrayListOf()
    lateinit var eventDates : HashSet<CalendarDay>
    lateinit var today : CalendarDay
    var selectedDate: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // ONCREATE 시 SharedPreferences 에 상세페이지 에서 메인 이동 저장 데이터 초기화
        val detailToMain : SharedPreferences = getSharedPreferences("detail_to_main", Activity.MODE_PRIVATE)
        val spEditor = detailToMain.edit()
        spEditor.putString("detail_to_main","FAIL")
        spEditor.apply()
        
        setContentView(R.layout.activity_calandar_main)
        today = CalendarDay.today()
        eventDates = hashSetOf()

        //레트로핏 주소 설정
        val retrofit = Retrofit.Builder().baseUrl("https://q3jz31f9wb.execute-api.ap-northeast-2.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)
        
        //현재 로그인 유저 정보 불러오기
        val owner : String = getSharedPreferences("owner", Activity.MODE_PRIVATE).getString("owner","") ?: ""
        val encodingOwner = Base64.encodeToString(owner.toByteArray(),Base64.NO_WRAP)
        var inputJsonString = "{\"owner\":\"$encodingOwner\",\"year\":\"${today.year}\",\"month\":\"${today.month + 1}\"}"
        Log.d("inputJsonString",inputJsonString)


        val dateText : TextView = findViewById(R.id.scheduleDateTextView)
        val refreshIcon = findViewById<ImageView>(R.id.refreshImage)
        val addScheduleButton = findViewById<Button>(R.id.addScheduleButton)
        val addGeneralButton = findViewById<Button>(R.id.addGeneralButton)

        //달력 뷰
        calendar = findViewById(R.id.calendarView)

        //한국어로 표현
        calendar.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        calendar.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        calendar.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))
        
        //달력 새로고침 기능 설정
        refreshIcon.setOnClickListener {
            val intent = intent
            finish()
            overridePendingTransition(0,0)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        // 회의 추가 버튼 설정
        addScheduleButton.setOnClickListener {
            val intent = Intent(this@CalandarMainActivity,ReservationApplyActivity::class.java)
            // 빈 스케줄 객체 만듬
            val scheduleData = Schedule(owner)
            var actionType = "create"
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType)
            startActivity(intent)
        }

        // 일반 일정 추가 버튼
        addGeneralButton.setOnClickListener {
            Log.d("일반 일정 만들기", "선택된 날짜: " + selectedDate);

            val intent = Intent(this@CalandarMainActivity,ReservationApplyActivity::class.java)
            // 빈 스케줄 객체 만듬
            val scheduleData = Schedule(owner)
            // 오늘 날짜 넣어줘야함
            scheduleData.date = selectedDate
            var actionType = "edit" // edit과 일반 일정의 flow가 같음
            intent.putExtra("scheduleData", scheduleData)
            intent.putExtra("actionType", actionType)
            startActivity(intent)
        }
        
        //레트로핏 HTTP 데이터 요청
        retrofitService.getScheduleListAPI(inputJsonString)?.enqueue(object : Callback<List<ScheduleDto>>{

            override fun onResponse(call: Call<List<ScheduleDto>>, response: Response<List<ScheduleDto>>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성공된 경우
                    val resultList: List<ScheduleDto> = response.body() ?: arrayListOf()
                    Log.d("일정 목록 조회", "onResponse 성공: $resultList")


                    for (index in resultList.indices){
                        val scheduleItem : ScheduleDto = resultList[index]
                        Log.d("result",scheduleItem.toString())

                        val owner = scheduleItem.owner
                        val scheduleId = scheduleItem.scheduleId
                        val year = scheduleItem.year
                        val month = scheduleItem.month
                        val date = scheduleItem.date
                        val title = scheduleItem.title
                        val detail = scheduleItem.detail
                        val startTime = scheduleItem.startTime
                        val endTime = scheduleItem.endTime
                        val place = scheduleItem.place
                        val participants : ArrayList<String> = scheduleItem.participants.split(',').toTypedArray().toCollection(arrayListOf())

                        totalScheduleArrayList.add(Schedule(
                            owner,scheduleId,year,month, date,title,detail,startTime, endTime , participants , place
                        ))

                        //이벤트가 있는날 점 표시하기 위해 set에 해당 날짜들 추가
                        eventDates.add(CalendarDay(year.toInt(),month.toInt() - 1,date.toInt()))

                    }


                    for (i in 0 until totalScheduleArrayList.size) {
                        val schedule = totalScheduleArrayList[i]
                        if (today.year.toString() == schedule.year && (today.month + 1).toString() == schedule.month && today.day.toString() == schedule.date){
                            selectedScheduleArrayList.add(schedule)
                        }
                    }

                    //recyclerViewAdapter 생성 및 recyclerView 그리기
                    recyclerViewAdapter = RecyclerViewAdapter(selectedScheduleArrayList, LayoutInflater.from(this@CalandarMainActivity), this@CalandarMainActivity)
                    recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                    recyclerView.adapter = recyclerViewAdapter
                    recyclerView.layoutManager = LinearLayoutManager(this@CalandarMainActivity)

                    //초기 선택 날짜 처리(현재 날짜 처리)
                    calendar.selectedDate = today

                    calendar.addDecorator(TodayDecorator())
                    calendar.addDecorator(SundayDecorator())
                    calendar.addDecorator(SaturDayDecorator())
                    calendar.addDecorator(EventDecorator(eventDates))

                    //초기 날짜 텍스트 설정
                    dateText.text = "${today.year}년 ${today.month + 1}월 ${today.day}일 (오늘)"


                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("일정 목록 조회", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<List<ScheduleDto>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("일정 목록 조회", "onFailure 에러: " + t.message.toString());
            }

        })


        //달력 날짜 선택시 설정
        calendar.setOnDateChangedListener { widget, date, selected ->
            if (date.year.toString() == today.year.toString() && date.month.toString() == today.month.toString() && date.day.toString() == today.day.toString()){
                dateText.text = "${date.year}년 ${date.month + 1}월 ${date.day}일 (오늘)"
            } else {
                dateText.text = "${date.year}년 ${date.month + 1}월 ${date.day}일"
            }

            selectedDate = date.year.toString() + (date.month + 1).toString().padStart(2, '0') + date.day.toString().padStart(2, '0')

            selectedScheduleArrayList = arrayListOf<Schedule>()

            // 1-9 일 경우 앞에 0 붙인다.
            var customDay = ""
            if (date.day.toString().length == 1) {
                customDay = "0" + date.day.toString()
            } else {
                customDay = date.day.toString()
            }

            for (i in 0 until totalScheduleArrayList.size) {
                val schedule = totalScheduleArrayList[i]
                if (date.year.toString() == schedule.year && (date.month + 1).toString() == schedule.month && customDay == schedule.date){
                    selectedScheduleArrayList.add(schedule)
                }
            }

            Log.d("날짜 선택",selectedScheduleArrayList.toString())

            //recyclerViewAdapter 생성 및 recyclerView 그리기
            recyclerViewAdapter = RecyclerViewAdapter(selectedScheduleArrayList, LayoutInflater.from(this@CalandarMainActivity),this@CalandarMainActivity)
            recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = recyclerViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@CalandarMainActivity)
        }

        //달력 월별 이동시 설정
        calendar.setOnMonthChangedListener { widget, date ->
            MonthDisplayDecorator(date)
            
            //월별 이동시 이벤트데이터 초기화
            eventDates = hashSetOf()
            totalScheduleArrayList = arrayListOf()
            // 다시 각 월에 맞는 정보를 받아온다.
            inputJsonString = "{\"owner\":\"$encodingOwner\",\"year\":\"${date.year}\",\"month\":\"${date.month + 1}\"}"
            retrofitService.getScheduleListAPI(inputJsonString)?.enqueue(object : Callback<List<ScheduleDto>>{

                override fun onResponse(call: Call<List<ScheduleDto>>, response: Response<List<ScheduleDto>>) {
                    if(response.isSuccessful){
                        // 정상적으로 통신이 성공된 경우
                        val resultList: List<ScheduleDto> = response.body() ?: arrayListOf()
                        Log.d("일정 목록 조회", "onResponse 성공: $resultList")

                        for (index in resultList.indices){
                            val scheduleItem : ScheduleDto = resultList[index]
                            Log.d("result",scheduleItem.toString())

                            val owner = scheduleItem.owner
                            val scheduleId = scheduleItem.scheduleId
                            val year = scheduleItem.year
                            val month = scheduleItem.month
                            val date = scheduleItem.date
                            val title = scheduleItem.title
                            val detail = scheduleItem.detail
                            val startTime = scheduleItem.startTime
                            val endTime = scheduleItem.endTime
                            val place = scheduleItem.place
                            val participants : ArrayList<String> = scheduleItem.participants.split(',').toTypedArray().toCollection(arrayListOf())

                            totalScheduleArrayList.add(Schedule(
                                owner,scheduleId,year,month, date,title,detail,startTime, endTime , participants , place
                            ))

                            //이벤트가 있는날 점 표시하기 위해 set에 해당 날짜들 추가
                            eventDates.add(CalendarDay(year.toInt(),month.toInt() - 1,date.toInt()))

                        }

                        for (i in 0 until totalScheduleArrayList.size) {
                            val schedule = totalScheduleArrayList[i]
                            if (date.year.toString() == schedule.year && (date.month + 1).toString() == schedule.month && date.day.toString() == schedule.date){
                                selectedScheduleArrayList.add(schedule)
                            }
                        }

                        //recyclerViewAdapter 생성 및 recyclerView 그리기
                        recyclerViewAdapter = RecyclerViewAdapter(selectedScheduleArrayList, LayoutInflater.from(this@CalandarMainActivity), this@CalandarMainActivity)
                        recyclerView.adapter = recyclerViewAdapter
                        recyclerView.layoutManager = LinearLayoutManager(this@CalandarMainActivity)

                        calendar.addDecorator(EventDecorator(eventDates))
                    }else{
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("일정 목록 조회", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<List<ScheduleDto>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("일정 목록 조회", "onFailure 에러: " + t.message.toString());
                }

            })
        }

        // SharedPreferences 에 날짜 저장
        val spYear : SharedPreferences = getSharedPreferences("year", Activity.MODE_PRIVATE)
        val spMonth : SharedPreferences = getSharedPreferences("month", Activity.MODE_PRIVATE)
        val spDay : SharedPreferences = getSharedPreferences("day", Activity.MODE_PRIVATE)

        val spYearEditor = spYear.edit()
        spYearEditor.putString("year",today.year.toString())
        spYearEditor.apply()

        val spMonthEditor = spMonth.edit()
        spMonthEditor.putString("month",(today.month + 1).toString())
        spMonthEditor.apply()

        val spDayEditor = spDay.edit()
        spDayEditor.putString("day",today.day.toString())
        spDayEditor.apply()

        // 이 화면은, 왼쪽에서 오른쪽으로 슬라이딩 하면서 켜집니다.
        overridePendingTransition(R.anim.from_left_enter, 0)

    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume 실행","SUCCESS")

        // 상세페이지에서 왔을 경우 새로고침
        val detailToMain : String = getSharedPreferences("detail_to_main", Activity.MODE_PRIVATE).getString("detail_to_main","") ?: ""
        if (detailToMain == "SUCCESS") {
            Log.d("ONRESUME 데이터 리셋 실행","SUCCESS")

            totalScheduleArrayList = arrayListOf()
            selectedScheduleArrayList = arrayListOf()

            //레트로핏 주소 설정
            val retrofit = Retrofit.Builder().baseUrl("https://q3jz31f9wb.execute-api.ap-northeast-2.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()
            val retrofitService = retrofit.create(RetrofitService::class.java)

            val spYear = getSharedPreferences("year",Activity.MODE_PRIVATE).getString("year",today.year.toString()) ?: today.year.toString()
            val spMonth = getSharedPreferences("month",Activity.MODE_PRIVATE).getString("month",(today.month + 1).toString()) ?: (today.month + 1).toString()
            val spDay = getSharedPreferences("day",Activity.MODE_PRIVATE).getString("day",today.day.toString()) ?: today.day.toString()

            //현재 로그인 유저 정보 불러오기
            val owner : String = getSharedPreferences("owner", Activity.MODE_PRIVATE).getString("owner","") ?: ""
            val encodingOwner = Base64.encodeToString(owner.toByteArray(),Base64.NO_WRAP)
            var inputJsonString = "{\"owner\":\"$encodingOwner\",\"year\":\"${spYear}\",\"month\":\"${spMonth}\"}"


            //레트로핏 HTTP 데이터 요청
            retrofitService.getScheduleListAPI(inputJsonString)?.enqueue(object : Callback<List<ScheduleDto>>{

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<List<ScheduleDto>>, response: Response<List<ScheduleDto>>) {
                    if(response.isSuccessful){
                        // 정상적으로 통신이 성공된 경우
                        val resultList: List<ScheduleDto> = response.body() ?: arrayListOf()
                        Log.d("일정 목록 조회", "onResponse 성공: $resultList")


                        for (index in resultList.indices){
                            val scheduleItem : ScheduleDto = resultList[index]
                            Log.d("ONRESUME result",scheduleItem.toString())

                            val owner = scheduleItem.owner
                            val scheduleId = scheduleItem.scheduleId
                            val year = scheduleItem.year
                            val month = scheduleItem.month
                            val date = scheduleItem.date
                            val title = scheduleItem.title
                            val detail = scheduleItem.detail
                            val startTime = scheduleItem.startTime
                            val endTime = scheduleItem.endTime
                            val place = scheduleItem.place
                            val participants : ArrayList<String> = scheduleItem.participants.split(',').toTypedArray().toCollection(arrayListOf())

                            totalScheduleArrayList.add(Schedule(
                                owner,scheduleId,year,month, date,title,detail,startTime, endTime , participants , place
                            ))

                            //이벤트가 있는날 점 표시하기 위해 set에 해당 날짜들 추가
                            eventDates.add(CalendarDay(year.toInt(),month.toInt() - 1,date.toInt()))

                        }


                        for (i in 0 until totalScheduleArrayList.size) {
                            val schedule = totalScheduleArrayList[i]
                            if (spYear == schedule.year && spMonth == schedule.month && spDay == schedule.date){
                                selectedScheduleArrayList.add(schedule)
                            }
                        }

                        //recyclerViewAdapter 생성 및 recyclerView 그리기
                        recyclerViewAdapter = RecyclerViewAdapter(selectedScheduleArrayList, LayoutInflater.from(this@CalandarMainActivity), this@CalandarMainActivity)
                        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        recyclerView.adapter = recyclerViewAdapter
                        recyclerView.layoutManager = LinearLayoutManager(this@CalandarMainActivity)

                        val selectedDate = CalendarDay(spYear.toInt(),spMonth.toInt() - 1,spDay.toInt())

                        //초기 선택 날짜 처리(현재 날짜 처리)
                        calendar.selectedDate = selectedDate

                        calendar.addDecorator(SundayDecorator())
                        calendar.addDecorator(SaturDayDecorator())
                        calendar.addDecorator(EventDecorator(eventDates))

                        val dateText : TextView = findViewById(R.id.scheduleDateTextView)

                        if (spYear == today.year.toString() && spMonth == (today.month + 1).toString() && spDay == today.day.toString()){
                            dateText.text = "${spYear}년 ${spMonth}월 ${spDay}일 (오늘)"
                        } else {
                            dateText.text = "${spYear}년 ${spMonth}월 ${spDay}일"
                        }

                    }else{
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("일정 목록 조회", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<List<ScheduleDto>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("일정 목록 조회", "onFailure 에러: " + t.message.toString());
                }

            })
        }
    }

    //뒤로가기 두번 클릭 시 앱 종료
    private var backPressedTime: Long = 0
    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }

}

class TodayDecorator(): DayViewDecorator {
    private var date = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.equals(date)!!
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(StyleSpan(android.graphics.Typeface.BOLD))
        view?.addSpan(RelativeSizeSpan(1.1f))
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#000000")))

    }
}

class SundayDecorator():DayViewDecorator {
    private val calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return (weekDay == Calendar.SUNDAY)
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object:ForegroundColorSpan(Color.RED){})
    }
}

class SaturDayDecorator():DayViewDecorator {

    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return (weekDay == Calendar.SATURDAY)
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object:ForegroundColorSpan(Color.BLUE){})
    }
}

class EventDecorator(private val eventDates: Collection<CalendarDay>): DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return eventDates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(10F, Color.parseColor("#003CDC")))
    }
}

class MonthDisplayDecorator(private val selectedMonth: CalendarDay?): DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.month != selectedMonth?.month
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object : ForegroundColorSpan(Color.GRAY){})
    }
}

//리사이클러뷰 아답터 설정
class RecyclerViewAdapter(
    private val scheduleList : ArrayList<Schedule>,
    private val inflater : LayoutInflater,
    private val context: Context
): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val titleText : TextView
        val timeText: TextView
        val countText : TextView

        init {
            titleText = itemView.findViewById(R.id.titleTextView)
            timeText = itemView.findViewById(R.id.timeTextView)
            countText = itemView.findViewById(R.id.countTextView)

        }



    }

    //뷰를 생성하는 부분
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.add_scedule_item_view, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return scheduleList.size
    }

    // view를 그리는 부분
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = scheduleList[position].title
        holder.timeText.text = "${scheduleList[position].startTime} ~ ${scheduleList[position].endTime}"
        holder.countText.text = "참석 인원: ${scheduleList[position].participants.size}명"

        holder.itemView.setOnClickListener{
            val intent = Intent(context,DetailScheduleActivity::class.java)
            intent.putExtra("scheduleData",scheduleList[position])
            context.startActivity(intent)
        }
    }
}

