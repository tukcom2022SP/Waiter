package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import kr.ac.tukorea.waiter.databinding.ActivityInfoRegistrationPageBinding
import kr.ac.tukorea.waiter.databinding.ActivityMainBinding

class Info_Registration_Page : AppCompatActivity() {

    private var mBinding : ActivityInfoRegistrationPageBinding? = null
    private val binding get() = mBinding!!
    lateinit var input_info_dlog : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInfoRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.PlusStoreBtn.setOnClickListener {
            input_info_dlog = View.inflate(this@Info_Registration_Page,R.layout.input_information,null)
            var menu = AlertDialog.Builder(this@Info_Registration_Page)
            //menu.setTitle("정보 입력")
            menu.setView(input_info_dlog)
            menu.setPositiveButton("확인",null)
            menu.show()

        }
    }




    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}