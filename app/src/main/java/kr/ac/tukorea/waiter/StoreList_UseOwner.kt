package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_store_list_use_owner.*
import kotlinx.android.synthetic.main.activity_waiting_list_page.*

class StoreList_UseOwner : AppCompatActivity() {

    var db2: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_list_use_owner)

        var storeListinfo = arrayListOf<StoreListInfo_useStoreListPage>()
        
        db2 = FirebaseFirestore.getInstance()

        val reserveInfo2 = db2.collection("rest_Info")

        reserveInfo2.addSnapshotListener { result, e ->
            if (result != null) {
                storeListinfo.clear()

                for (data in result) {

                    storeListinfo.addAll(
                        listOf(
                            StoreListInfo_useStoreListPage(
                                data.data["storeName"].toString(),
                                data.data["roadNameAddress"].toString()
                            )
                        )
                    )

                }

            } else {
                Log.d("TAG", "fail")
            }
            Log.d("datachk",storeListinfo[0].roadNameAddress)
            val Adapter = StoreListAdapter(this, storeListinfo)
            StorelistView.adapter = Adapter
        }
    }
}