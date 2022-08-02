package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.ac.tukorea.waiter.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signinbutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }
}