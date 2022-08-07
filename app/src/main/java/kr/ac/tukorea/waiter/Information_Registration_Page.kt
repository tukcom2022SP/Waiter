package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kr.ac.tukorea.waiter.databinding.ActivityInformationRegistrationPageBinding

class Information_Registration_Page : AppCompatActivity() {

    private var mbinding : ActivityInformationRegistrationPageBinding? = null
    private val binding get() = mbinding!!

    private var StoreNameStirng : String? = null // 입력 값 저장할 변수들 얘들을 디비로?
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

        binding.registrationBtn.setOnClickListener {
            if(binding.storeNameEdit.length() == 0 || binding.storeAddressEdit.length()==0 ||  binding.storeCorpNumEdit.length()==0 ){
                Toast.makeText(this, "값을 모두 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else{
                //activName.receiveData()
                StoreNameStirng = binding.storeNameEdit.toString()
                AddressString = binding.storeAddressEdit.toString()
                CorpNumString = binding.storeCorpNumEdit.toString()
                Toast.makeText(this, "입력 완료", Toast.LENGTH_LONG).show()
                val intent = Intent(this,Waiting_List_Page::class.java)
                intent.putExtra("storeName",binding.storeNameEdit.text.toString())
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        mbinding = null
        super.onDestroy()
    }
}