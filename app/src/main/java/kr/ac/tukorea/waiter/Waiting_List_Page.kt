package kr.ac.tukorea.waiter

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_waiting_list_page.*
import kr.ac.tukorea.waiter.databinding.ActivityWaitingListPageBinding

class Waiting_List_Page : AppCompatActivity() {

    private var Wbinding : ActivityWaitingListPageBinding? = null
    private val binding get() = Wbinding!!

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1,menu)
        return true
    }

    var waitingListInfo = arrayListOf<WaitingListInfo_useWaitingPage>(
        WaitingListInfo_useWaitingPage("chu","010-1234-1234","5"),
        WaitingListInfo_useWaitingPage("kim","010-1234-1234","3"),
        WaitingListInfo_useWaitingPage("lee","010-1234-1234","12")

    ) // 리스트 항목 입력

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Wbinding = ActivityWaitingListPageBinding.inflate(layoutInflater)
        setContentView(binding.root)  //바인딩 연결

        val Adapter = WaitingListAdapter(this,waitingListInfo)
        listView.adapter = Adapter


    }

    override fun onStart() {
        super.onStart()

        //setContentView(R.layout.activity_waiting_list_page)
        var reintent = intent

        val storeName = reintent.getStringExtra("storeName").toString()
        binding.test1.text = storeName
        Toast.makeText(this, storeName, Toast.LENGTH_LONG).show()
    }
}