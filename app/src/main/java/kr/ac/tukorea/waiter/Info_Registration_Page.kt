package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.tukorea.waiter.databinding.ActivityInfoRegistrationPageBinding
import kr.ac.tukorea.waiter.databinding.ActivityMainBinding

class Info_Registration_Page : AppCompatActivity() {

    private var mBinding : ActivityInfoRegistrationPageBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInfoRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.PlusStoreBtn.setOnClickListener {

        }
    }




    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}