package kr.ac.tukorea.waiter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kr.ac.tukorea.waiter.databinding.StorelistLayoutBinding
import kr.ac.tukorea.waiter.databinding.WaitingListPageLayoutBinding

class StoreListAdapter ( val context: Context, val StoreListInfo: ArrayList<StoreListInfo_useStoreListPage>):BaseAdapter(){

    override fun getCount(): Int {
        return StoreListInfo.size
    }

    override fun getItem(position: Int): Any {
        return StoreListInfo[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val binding2 = StorelistLayoutBinding.inflate(LayoutInflater.from(context))

        val storeinfo =StoreListInfo[position]
        binding2.stroeListName.text = storeinfo.storeName
        binding2.StoreListAddress.text = storeinfo.roadNameAddress


        return binding2.root


    }

}
