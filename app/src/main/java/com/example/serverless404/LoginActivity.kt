package com.example.serverless404

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Objects

class LoginActivity : AppCompatActivity() {
    
    //화면 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val idEditText = findViewById<EditText>(R.id.idEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButon = findViewById<Button>(R.id.loginButton)
        val retrofit = Retrofit.Builder().baseUrl("https://hlmaacjf8c.execute-api.ap-northeast-2.amazonaws.com/").addConverterFactory(GsonConverterFactory.create()).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        loginButon.setOnClickListener {
            var myToast: Toast
            if (idEditText.text.isEmpty()) {
                myToast = CustomToast(applicationContext, "아이디를 입력해주세요.")
                myToast.setGravity(Gravity.CENTER, 0, 0)
                myToast.show()
            } else if (passwordEditText.text.isEmpty()) {
                myToast = CustomToast(applicationContext, "비밀번호를 입력해주세요.")
                myToast.setGravity(Gravity.CENTER, 0, 0)
                myToast.show()
            } else {
                retrofitService.login("{\"id\":\"${idEditText.text}\", \"pw\":\"1234\"}")
                    ?.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공된 경우
                                val resultString: String = response.body()?.string() ?: ""
                                Log.d("로그인", "onResponse 성공: $resultString")

                                if (resultString.contains("SUCCESS")) {

                                    val owner = resultString.substring(resultString.indexOf("\"owner\"") + 9,resultString.length - 2)
                                    Log.d("로그인 유저 정보",owner)

                                    // SharedPreferences 에 로그인 유저 정보 저장
                                    val ownerInfo : SharedPreferences = getSharedPreferences("owner",Activity.MODE_PRIVATE)
                                    val spEditor = ownerInfo.edit()
                                    spEditor.putString("owner",owner)
                                    spEditor.apply()

                                    //메인 달력 액티비로 넘어가기
                                    val intent =
                                        Intent(this@LoginActivity, CalandarMainActivity::class.java)
                                    intent.putExtra("SUCCESS", "로그인 성공")
                                    startActivity(intent)
                                    //로그인 성공시 해당 activity 종료
                                    finish()
                                } else {
                                    myToast =
                                        CustomToast(applicationContext, "아이디와 패스워드를 다시 확인해 주세요")
                                    myToast.setGravity(Gravity.CENTER, 0, 0)
                                    myToast.show()
                                }
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                Log.d("로그인", "onResponse 실패")

                                myToast = CustomToast(applicationContext, "아이디와 패스워드를 다시 확인해 주세요.")
                                myToast.setGravity(Gravity.CENTER, 0, 0)
                                myToast.show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                            Log.d("로그인", "onFailure 에러: " + t.message.toString());

                            myToast = CustomToast(applicationContext, "서버와 통신에 실패하였습니다.")
                            myToast.setGravity(Gravity.CENTER, 0, 0)
                            myToast.show()
                        }
                    })
            }
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

class CustomToast(context: Context, message: String) : Toast(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null)
        view.findViewById<TextView>(R.id.text_toast).apply {
            text = message
        }
        setView(view)
        duration = Toast.LENGTH_SHORT
    }
}