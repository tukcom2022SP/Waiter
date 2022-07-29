package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class Waiting_List_Page : AppCompatActivity() {

    lateinit var plus_button : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_list_page)
    }
}