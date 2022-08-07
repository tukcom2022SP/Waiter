package kr.ac.tukorea.waiter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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

        val view: View = LayoutInflater.from(context).inflate(R.layout.waiting_list_page_layout,null)

        val name = binding.waitingName1
        val phoneNum = binding.waitingPhone1
        val num = binding.waitingNum1

        val userinfo =WaitingListInfo[position]

        name.text = userinfo.customername
        phoneNum.text = userinfo.phoneNum
        num.text = userinfo.customerNum

        return view
    }

}