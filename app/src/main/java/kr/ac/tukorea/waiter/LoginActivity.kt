package kr.ac.tukorea.waiter

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kr.ac.tukorea.waiter.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding
    var db: FirebaseFirestore = Firebase.firestore


    data class UserInfo(
        val x_y: ArrayList<String> = ArrayList(),
        //val passwdCk: String? = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        val map_intent = Intent(this, MapPage::class.java)


        binding.loginbutton.setOnClickListener {
            val userEmail = binding.userid.text.toString()
            val password = binding.password.text.toString()

            if(binding.userid.text.toString().equals("")
                ||binding.password.text.toString().equals("")
            ) {
                Toast.makeText(this, "로그인에 필요한 정보를 모두 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                doLogin(userEmail, password, map_intent)
            }
        }

//clickable 써보자
        binding.signinButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    // 로그인 시 실행되는 함수
    private fun doLogin(userEmail: String, password: String, map_intent:Intent) {
        Log.d("data33",Firebase.auth.currentUser?.uid.toString() )
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if (it.isSuccessful) {
                    db.collection("user").document(Firebase.auth.currentUser?.uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                     if (documents != null) { // documents 값이 존재할 때
                        var userType = documents.get("userType").toString()
                        var x_y = documents.toObject<UserInfo>()  //인텐트로 페이지를 넘겨줄 때 사용자의 정보도 같이 넘겨주기 위함
                         Log.d("Login2222", "${userType}")
                         if (userType.equals("customer")) {
                             map_intent.putExtra("name2", "${documents.get("signName")}")
                             map_intent.putExtra("phone2", "${documents.get("phoneNum")}")
                             Log.d("Login22", "${documents.get("signName")}")
                             startActivity(
                                 map_intent
                             )
                         }
                         else if (userType.equals("owner")){
                             if (x_y != null) {
                                 if(!x_y.x_y.isEmpty()) {
                                     //Log.d("현민  ", "${x_y.x_y}")
                                     var aa = arrayListOf<String>()
                                     aa.addAll(x_y.x_y)
                                     val intent = Intent(this, StoreList_UseOwner::class.java)
                                     intent.putStringArrayListExtra("x_y",aa)
                                     startActivity(intent)
                                 }
                                 else {
                                     startActivity(
                                         Intent(this, Information_Registration_Page::class.java)
                                     )
                                 }
                             }
                         }
                         finish()
                     }
                     else { // 값이 없을 때
                         Log.d(TAG,"NO such document")
                     }
                    }
                        .addOnFailureListener { exception -> // db 접근을 하지 못했을 때
                            Log.d(TAG, "get failed with ", exception)
                        }
                }
                else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "아이디 혹은 비밀번호 오류로 인한 로그인 실패!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}