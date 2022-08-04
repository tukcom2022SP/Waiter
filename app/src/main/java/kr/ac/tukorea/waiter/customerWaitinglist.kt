package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import kr.ac.tukorea.waiter.databinding.ActivityCustomerWaitinglistBinding
import kr.ac.tukorea.waiter.databinding.ActivityInformationRegistrationPageBinding

class customerWaitinglist : AppCompatActivity() {

    private var Cbinding : ActivityCustomerWaitinglistBinding? = null
    private val binding get() = Cbinding!!

    private var StoreNameStirng : String? = null
    private var waitingPhone : String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1,menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_waitinglist)
    }
}