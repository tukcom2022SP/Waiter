package kr.ac.tukorea.waiter

import android.app.Activity
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_waiting_list_page.*
import kr.ac.tukorea.waiter.databinding.ActivityWaitingListPageBinding


class Waiting_List_Page : AppCompatActivity() {

    private var Wbinding: ActivityWaitingListPageBinding? = null
    private val binding get() = Wbinding!!

    var db: FirebaseFirestore = Firebase.firestore

    data class UserInfo(
        val index: String? = null,
        val name: String? = null,
        val phone: String? = null
    )


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var waitingListInfo = arrayListOf<WaitingListInfo_useWaitingPage>()

        db = FirebaseFirestore.getInstance()

        val reserveInfo = db.collection("rest_Info").document("abc")
            .collection("reservation")

        reserveInfo.get().addOnSuccessListener { result ->

            if (result != null) {
                for (data in result) {
                    waitingListInfo.addAll(
                        listOf(
                            WaitingListInfo_useWaitingPage(
                                data.get("name").toString(),
                                data.get("phone").toString(),
                                data.get("customerNum").toString().toInt(),
                                data.get("index").toString().toInt()
                            )
                        )
                    )

                }
            } else {
                Log.d("TAG", "fail")
            }
            val Adapter = WaitingListAdapter(this, waitingListInfo)
            listView.adapter = Adapter

        }


        Wbinding = ActivityWaitingListPageBinding.inflate(layoutInflater)
        setContentView(binding.root)  //바인딩 연결


    }

    override fun onStart() {
        super.onStart()

        setContentView(R.layout.activity_waiting_list_page)
        var reintent = intent


    }
}