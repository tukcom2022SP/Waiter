package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_store_list_use_owner.*
import kotlinx.android.synthetic.main.activity_user_page.*

class UserPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        var db: FirebaseFirestore = Firebase.firestore

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        var userlistinfo = arrayListOf<UserReserve_useUserPage>()
        db = FirebaseFirestore.getInstance()

        //intent로 유저 아이디

        val UserResrveInfo = db.collection("user").document(
            "CLrm0EqWbwPSQYMqvPLX2A2I0Wn1").collection("reserveInfo")

        UserResrveInfo.addSnapshotListener{ result, e ->
            if (result != null) {
                userlistinfo.clear()

                for (data in result) {
                    userlistinfo.addAll(
                        listOf(
                            UserReserve_useUserPage(
                                data.get("po").toString().toInt(),
                                data.get("x_y").toString(),
                                data.get("storeName").toString()

                            )
                        )
                    )
                }
            } else {
                Log.d("TAG", "fail")
            }
            //Log.d("datachk",userlistinfo[0].roadNameAddress)
            val Adapter = UserPageAdapter(this, userlistinfo)
            userlist.adapter = Adapter
        }


    }
}