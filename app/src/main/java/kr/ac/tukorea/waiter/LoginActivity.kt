package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
//import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import kr.ac.tukorea.kyungeun_login.databinding.ActivityLoginBinding
import kr.ac.tukorea.waiter.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1,menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener {
            val userEmail = binding.userid.text.toString()
            val password = binding.password.text.toString()
            //doLogin(userEmail, password)
            val intent = Intent(this, MapPage::class.java)
            startActivity(intent)

        }
//clickable 써보자


        binding.signinButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
//    private fun doLogin(userEmail: String, password: String) {
//        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
//            .addOnCompleteListener(this) { // it: Task<AuthResult!>
//                if (it.isSuccessful) {
//                    startActivity(
//                        Intent(this, MainActivity::class.java)
//                    )
//                    finish()
//                } else {
//                    Log.w("LoginActivity", "signInWithEmail", it.exception)
//                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
}