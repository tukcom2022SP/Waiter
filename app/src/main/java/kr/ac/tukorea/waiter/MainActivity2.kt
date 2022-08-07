package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ResultSearchKeyword
import android.util.Log

class MainActivity2 : AppCompatActivity() {
    companion object{
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK e2ff78b2e20ee43f72827e3e379c2191"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchKeyword("스타벅스")
    }
    private fun searchKeyword(place_name: String)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call= api.getSearchKeyword(API_KEY,place_name)

        call.enqueue(object: Callback<ResultSearchKeyword>{
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                Log.d("Test","성공: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
                val x = response.body()?.documents?.get(0)?.x
                val y = response.body()?.documents?.get(0)?.y
                println("x : ${x}")
                println("y : ${y}")
            }
            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("MainActivity","실패 ${t.message}")
            }
        })
    }
}


