package com.kangtong.btgtouristregister.view.tool

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kangtong.btgtouristregister.R
import com.kangtong.btgtouristregister.model.Tourist
import kotlinx.android.synthetic.main.fragment_tool_list.*

/**
 * 工具箱页面
 */
class ToolFragment : Fragment() {

    companion object {

        fun newInstance(): ToolFragment {
            val fragment = ToolFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    // 所有游客信息
    private val mTouristList = mutableListOf<Tourist>()
    private val mAdapter = MyToolAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tool_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        loadData(true)
    }

    private fun setupView() {
        context?.run {
            fab_print.setOnClickListener { toast("表->Excel") }

            recycler_tourist_table_information.layoutManager = LinearLayoutManager(this)
            recycler_tourist_table_information.adapter = mAdapter
        }
    }

    /**
     * 加载数据
     * @param isRefresh: 是否为刷新数据, 默认为 false
     */
    private fun loadData(isRefresh: Boolean = false) {
        // 查询数据库
        val list = mutableListOf<Tourist>().apply {
            for (i in 1..20) {
                add(Tourist().apply { peopleName = "$i" })
            }
        }
        // 更新 UI
        if (isRefresh) {
            mAdapter.setData(list)
        } else {
            mAdapter.appendData(list)
        }
    }
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}