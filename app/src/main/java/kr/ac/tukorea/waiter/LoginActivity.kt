package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import kr.ac.tukorea.kyungeun_login.databinding.ActivityLoginBinding
import kr.ac.tukorea.waiter.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

            binding.signinButton.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)

            }
        }
    }
}