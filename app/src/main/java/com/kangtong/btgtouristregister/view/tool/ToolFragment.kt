package com.kangtong.btgtouristregister.view.tool

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kangtong.btgtouristregister.R
import com.kangtong.btgtouristregister.model.Tourist
import com.kangtong.btgtouristregister.view.excel.ExcelActivity
import kotlinx.android.synthetic.main.fragment_tool_list.*

/**
 * 工具箱页面
 */
class ToolFragment : Fragment() {

    companion object {

        private const val QUERY_BY_ALL = 1
        private const val QUERY_BY_DATE = 2
        private const val QUERY_BY_GUIDE = 3

        fun newInstance(): ToolFragment {
            val fragment = ToolFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private val mAdapter = MyToolAdapter()

    private var mGuideName = ""
    private var mDate = ""
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
            fab_print.setOnClickListener { ExcelActivity.start(this, mGuideName, mDate) }
            recycler_tourist_table_information.layoutManager = LinearLayoutManager(this)
            recycler_tourist_table_information.adapter = mAdapter
        }

        val pTypeCount = ll_query_type.childCount
        for (index in 0 until pTypeCount) {
            val button = ll_query_type.getChildAt(index)
            button.setOnClickListener {
                loadData(true)
            }
        }
    }


    /**
     * 加载数据
     * @param isRefresh: 是否为刷新数据, 默认为 false
     */
    private fun loadData(isRefresh: Boolean = false) {
//         查询数据库
        query() {
            // 检测空值
            if (it.isEmpty()) {
                context?.toast("数据为空~")
                return@query
            }
            // 更新 UI
            if (isRefresh) {
                mAdapter.setData(it)
            } else {
                mAdapter.appendData(it)
            }
        }

        // TODO - DQ - 2019/4/2 : 以下为测试代码, 本地数据库有值时, 请删除, 并还原以上查询数据库的代码
//        mutableListOf<Tourist>().apply {
//            for (i in 1..10) {
//                add(Tourist().apply { peopleName = "$i" })
//            }
//        }.also {
//            // 更新 UI
//            if (isRefresh) {
//                mAdapter.setData(it)
//            } else {
//                mAdapter.appendData(it)
//            }
//        }
    }


    /**
     * 查询数据
     * @param type: 查询类型
     *      |-- QUERY_BY_ALL    : 全部(默认)
     *      |-- QUERY_BY_DATE   : 按日期
     *      |-- QUERY_BY_GUIDE  : 按导游
     */
    private fun query(result: (list: MutableList<Tourist>) -> Unit) {
        TouristManage.getInstance().query(mGuideName, mDate) {
            result(it)
        }
    }
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}