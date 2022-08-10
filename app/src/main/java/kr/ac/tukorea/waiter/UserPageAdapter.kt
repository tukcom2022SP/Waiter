package kr.ac.tukorea.waiter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kr.ac.tukorea.waiter.databinding.UserpageLayoutBinding
import kr.ac.tukorea.waiter.databinding.WaitingListPageLayoutBinding

class UserPageAdapter(val context: Context, val UserReserve: ArrayList<UserReserve_useUserPage>):BaseAdapter() {
    override fun getCount(): Int {
        return UserReserve.size
    }

    override fun getItem(position: Int): Any {
        return UserReserve[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val binding = UserpageLayoutBinding.inflate(LayoutInflater.from(context))

        val userinfo =UserReserve[position]
        binding.userStoreNum.text = userinfo.po.toString()
        binding.position.text = userinfo.x_y
        binding.userStoreName.text = userinfo.storeName


        return binding.root


    }

}