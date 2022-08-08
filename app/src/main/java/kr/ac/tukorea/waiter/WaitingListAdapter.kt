package kr.ac.tukorea.waiter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kr.ac.tukorea.waiter.databinding.ActivityWaitingListPageBinding
import kr.ac.tukorea.waiter.databinding.WaitingListPageLayoutBinding


class WaitingListAdapter(val context: Context, val WaitingListInfo: ArrayList<WaitingListInfo_useWaitingPage>):BaseAdapter() {

    override fun getCount(): Int {
        return WaitingListInfo.size
    }

    override fun getItem(position: Int): Any {
        return WaitingListInfo[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val binding = WaitingListPageLayoutBinding.inflate(LayoutInflater.from(context))

        val userinfo =WaitingListInfo[position]
        binding.waitingName1.text = userinfo.name
        binding.waitingNum1.text = userinfo.customerNum.toString()
        binding.waitingPhone1.text = userinfo.phone

        return binding.root


    }

}