package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.ac.tukorea.waiter.databinding.ActivityInformationRegistrationPageBinding

class Information_Registration_Page : AppCompatActivity() {
    var db: FirebaseFirestore = Firebase.firestore
    private var mbinding : ActivityInformationRegistrationPageBinding? = null
    private val binding get() = mbinding!!

    // 입력 값 저장할 변수들 얘들을 디비로?
    private var AddressString : String? = null
    private var CorpNumString : String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1,menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mbinding = ActivityInformationRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        binding.searchAddress.setOnClickListener {

//            val restMap = hashMapOf(
//                // 식당 이름, 주소, x, y, 연락처 DB에 넣기
//                "상호명" to binding.storeAddressEdit.text.toString(),
//                "도로명 주소" to binding.text.toString(),
//                "지번 주소" to binding.text.toString(),
//                "연락처" to binding.text.toString(),
//                "경도 x좌표" to binding.text.toString(),
//                "위도 y좌표" to binding.text.toString()
//            )
//
//            db.collection("restInfo").add(restMap)
//                .addOnSuccessListener {
//
//                }
//
//
      }

        binding.registrationBtn.setOnClickListener {
            if(binding.storeAddressEdit.text.toString().equals("")
                || binding.storeCorpNumEdit.text.toString().equals("") ){
                Toast.makeText(this, "식당 등록에 필요한 정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                //activName.receiveData()
                AddressString = binding.storeAddressEdit.toString()
                CorpNumString = binding.storeCorpNumEdit.toString()



                Toast.makeText(this, "입력 완료", Toast.LENGTH_LONG).show()
                val intent = Intent(this,Waiting_List_Page::class.java)
                //intent.putExtra("storeName",binding.storeNameEdit.text.toString())
                startActivity(intent)
            }
        }
    }
    override fun onDestroy() {
        mbinding = null
        super.onDestroy()
    }
}