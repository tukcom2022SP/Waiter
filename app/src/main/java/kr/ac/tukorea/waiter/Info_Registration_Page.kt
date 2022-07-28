package kr.ac.tukorea.waiter

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.DialogFragment
import kr.ac.tukorea.waiter.databinding.ActivityInfoRegistrationPageBinding
import kr.ac.tukorea.waiter.databinding.ActivityMainBinding
import kr.ac.tukorea.waiter.databinding.InputInformationBinding

class CustomDialog : DialogFragment(){
    private var infoBinding : InputInformationBinding? = null
    private val subbinding get() = infoBinding!!  //fragment에서 사용

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        infoBinding = InputInformationBinding.inflate(inflater,container,false)
        val view = subbinding.root

        subbinding.storeNameEdit.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        infoBinding = null
    }
}

class Info_Registration_Page : AppCompatActivity() {

    private var mBinding : ActivityInfoRegistrationPageBinding? = null
    private val binding get() = mBinding!!  //activity에서 바인딩 사용

    //lateinit var input_info_dlog : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityInfoRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.PlusStoreBtn.setOnClickListener {

            val input_info_dlog = CustomDialog()
            input_info_dlog.show(supportFragmentManager,"CustomDialog")

//            input_info_dlog = View.inflate(this@Info_Registration_Page,R.layout.input_information,null)
//            var menu = AlertDialog.Builder(this@Info_Registration_Page)
//            //menu.setTitle("정보 입력")
//            menu.setView(input_info_dlog)
//            menu.setPositiveButton("확인",null)
//            menu.show()

//            DialogInterface.OnCancelListener {
//                if(subbinding.storeNameEdit.length()==0){
//                    Toast.makeText(this,"값을 모두 입력해주세요",Toast.LENGTH_LONG).show()
//                }
//            }

        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}