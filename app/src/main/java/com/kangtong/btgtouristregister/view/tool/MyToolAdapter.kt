package com.kangtong.btgtouristregister.view.tool

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kangtong.btgtouristregister.R
import com.kangtong.btgtouristregister.model.Tourist
import kotlinx.android.synthetic.main.fragment_tool.view.*

/**
 * 游客表显示
 */
class MyToolAdapter(
        private val dataList: MutableList<Tourist> = mutableListOf()
) : RecyclerView.Adapter<MyToolAdapter.ViewHolder>() {

    /**
     * 初始化数据
     */
    fun setData(pDataList: MutableList<Tourist>) {
        dataList.clear()
        appendData(pDataList)
    }

    /**
     * 追加数据
     */
    fun appendData(pDataList: MutableList<Tourist>) {
        dataList.addAll(pDataList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_tool, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Tourist) {
            item.run {
                mView.tv_guide.text = guideName
                mView.tv_date.text = addTime
                mView.tv_tourist.text = "游客姓名: $peopleName"
                mView.root_view.setOnClickListener {
                    it.context.toast("点击了 $peopleName 这一项")
                }
            }
        }
    }
}