package kr.ac.tukorea.waiter

import ResultSearchKeyword
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kr.ac.tukorea.waiter.Information_Registration_Page.Companion.API_KEY
import kr.ac.tukorea.waiter.Information_Registration_Page.Companion.BASE_URL
import kr.ac.tukorea.waiter.databinding.ActivitySearchPageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Waiter_Search : AppCompatActivity(){
    private lateinit var binding: ActivitySearchPageBinding
    val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""
    private lateinit var myExam :Exam
    var user_name = ""
    var phone_num = ""
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.customermenu, menu)
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        val intent = Intent(this, Information_Registration_Page::class.java)

        // 리사이클러 뷰
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

        if (intent.hasExtra("user_name")){
            user_name = intent.getStringExtra("user_name").toString()
            phone_num = intent.getStringExtra("phoneNum").toString()

        }

        fun searchKeyword(place_name: String) {
            //API설정
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(KakaoAPI::class.java)
            val call = api.getSearchKeyword(API_KEY, place_name)

            call.enqueue(object : Callback<ResultSearchKeyword> {
                //만약에 API와 통신성공시
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    Log.d("Test", "성공: ${response.raw()}")//로그찍기
                    Log.d("Test", "Body: ${response.body()}")//로그찍기
                    val x = response.body()?.documents?.get(0)?.x//x 확인 값
                    val y = response.body()?.documents?.get(0)?.y//y 확인 값
                    addItemsAndMarkers(response.body())
                }
                //만약에 API와 통신실패시
                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.w("MainActivity", "실패 ${t.message}")
                }
            })
        }
        binding.btnSearch1.setOnClickListener {
            keyword = binding.searchText1.text.toString()
            pageNumber = 1
            searchKeyword(keyword)
            binding.hintText.visibility = View.GONE
            binding.imageL.visibility= View.GONE
        }
    }
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            var posx = ""
            var posy = ""
            listItems.clear()
            Log.d("로그55", "${searchResult}")//로그 찍기
            for (document in searchResult!!.documents)
            // 해당 결과들이 documents 에 있으면
            {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.phone,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)//item에 있는내용 list로 넘기기
                listAdapter.notifyDataSetChanged()//listadapter에 변경사항 알리기
                Log.d("로그3", "${item}")//로그찍어보기
                posx = document.x
                posy = document.y
                Log.d("한",posx)
            }
            var mapIntent = Intent(this, MapPage::class.java)
            listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
                override fun onClick(v: View, position: Int) {
                    mapIntent.putExtra("user_name", user_name)
                    mapIntent.putExtra("phoneNum",phone_num)
                    mapIntent.putExtra("name",listItems[position].name)
                    mapIntent.putExtra("address",listItems[position].address)
                    mapIntent.putExtra("road",listItems[position].road)
                    mapIntent.putExtra("x",listItems[position].x)
                    mapIntent.putExtra("y",listItems[position].y)
                    myExam = Exam(listItems[position].name,listItems[position].address,listItems[position].road,listItems[position].phone,listItems[position].x.toString(),listItems[position].y.toString())
                    mapIntent.putExtra("examKey",myExam)
                    startActivity(mapIntent)
                }
            })
        }
        else
        {
            // 검색 결과가 없을 때 toast 메세지
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
    //뒤로 가기 버튼을 2번 눌렀을 때 앱 종료 가능
    private var backBtnTime: Long = 0

    override fun onBackPressed() {
        //super.onBackPressed()
        var curTime = System.currentTimeMillis();
        var gapTime = curTime - backBtnTime
        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }
}


