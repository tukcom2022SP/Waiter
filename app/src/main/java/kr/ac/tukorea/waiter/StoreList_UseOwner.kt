package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_store_list_use_owner.*
import kr.ac.tukorea.waiter.databinding.ActivityStoreListUseOwnerBinding

class StoreList_UseOwner : AppCompatActivity() {

    var db2: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_store_list_use_owner)

        var storeListinfo = arrayListOf<StoreListInfo_useStoreListPage>()
        
        db2 = FirebaseFirestore.getInstance()
        val intent2 = Intent(this, Waiting_List_Page::class.java)

        val reserveInfo2 = db2.collection("rest_Info")
        val x_y = intent.getStringArrayListExtra("x_y")
        Log.d("x_y2", "${x_y}")

        reserveInfo2.addSnapshotListener { result, e ->
            if (result != null) {
                storeListinfo.clear()
                for (data in result) {
                    if (x_y != null) {
                        if (x_y.contains(data.id)){
                             storeListinfo.addAll(
                                listOf(
                                    StoreListInfo_useStoreListPage(
                                        data.data["storeName"].toString(),
                                        data.data["roadNameAddress"].toString(),
                                        data.data["longitude_x"].toString().toDouble(),
                                        data.data["latitude_y"].toString().toDouble()
                                    )
                                )
                            )
                        }
                    }
                }
            } else {
                Log.d("TAG", "fail")
            }
            Log.d("datachk",storeListinfo[0].roadNameAddress)
            val Adapter = StoreListAdapter(this, storeListinfo)
            StorelistView.adapter = Adapter

            StorelistView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val selection = parent.getItemAtPosition(position) as StoreListInfo_useStoreListPage
                Log.d("Testdata", "${position}")
                Toast.makeText(this, selection.storeName, Toast.LENGTH_SHORT).show()
                intent2.putExtra("x_y", "${selection.longitude_x}_${selection.latitude_y}")
                startActivity(intent2)
            }

        }
    }

    override fun onStart() {  //메뉴 페이지
        super.onStart()

        val binding = ActivityStoreListUseOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val logIntent = Intent(this, LoginActivity::class.java)
        val registIntent = Intent(this, Information_Registration_Page::class.java)

        binding.menubtn.setOnClickListener {
            var popmenu = PopupMenu(applicationContext,it)
            menuInflater?.inflate(R.menu.ownermenu,popmenu.menu)
            popmenu.show()
            popmenu.setOnMenuItemClickListener{ item ->
                when(item.itemId){
                    R.id.logout -> {
                        Toast.makeText(applicationContext,"로그아웃 되었습니다.\n다시 로그인해주요",Toast.LENGTH_SHORT).show()
                        Firebase.auth.signOut()
                        startActivity(logIntent)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.addNewRestaurant -> {
                        Toast.makeText(applicationContext,"사장님의 식당을 추가해주세요",Toast.LENGTH_SHORT).show()
                        startActivity(registIntent)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.ite2 -> {
                        Toast.makeText(applicationContext, "3", Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }
                    else-> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }
    }
}