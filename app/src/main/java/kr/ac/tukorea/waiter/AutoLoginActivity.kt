package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kr.ac.tukorea.waiter.databinding.ActivityLoginBinding
import java.util.*

class AutoLoginActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding
    var db: FirebaseFirestore = Firebase.firestore

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
                    auth = Firebase.auth
                    binding = ActivityLoginBinding.inflate(layoutInflater)
                    db = FirebaseFirestore.getInstance()

            //이미 로그인한 적이 있는지 확인합니다.
            if (userid == null) {

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val intent: Intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 2000)

            }else{

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val intent: Intent = Intent(applicationContext, MapPage::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 2000)

            }
        }
    }
