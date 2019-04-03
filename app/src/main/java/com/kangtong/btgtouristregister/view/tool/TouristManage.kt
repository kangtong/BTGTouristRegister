package com.kangtong.btgtouristregister.view.tool

import com.kangtong.btgtouristregister.model.Tourist
import org.litepal.LitePal

/**
 * 游客列表的工具类
 * @author DQDana
 * @since 2019/3/31 10:02 PM
 */
class TouristManage private constructor() {

    // 以下两个方法, 实现单例
    companion object {
        fun getInstance(): TouristManage {
            return TouristManageHolder.holder
        }
    }

    private object TouristManageHolder {
        val holder by lazy { TouristManage() }
    }

    // 以下为 增删改查

    fun insert() {

    }

    fun delete() {

    }

    fun update() {

    }

    fun queryByGuide(guide: String, result: (list: MutableList<Tourist>) -> Unit) {
        // TODO - DQ - 2019/4/2 : 这里会有崩溃, 是因为表数据没有该列
        // no such column: guide (Sqlite code 1): , while compiling: SELECT * FROM tourist WHERE guide = ?, (OS error - 2:No such file or directory)
        LitePal.where("guideName = ?", guide)
                .findAsync(Tourist::class.java)
                .listen {
                    result(it)
                }
    }

    fun queryByDate(date: String, result: (list: MutableList<Tourist>) -> Unit) {
        // TODO - DQ - 2019/4/2 : 这里会有崩溃, 是因为表数据没有该列
        // no such column: guide (Sqlite code 1): , while compiling: SELECT * FROM tourist WHERE date = ?, (OS error - 2:No such file or directory)
        LitePal.where("addTime = ?", date)
                .findAsync(Tourist::class.java)
                .listen {
                    result(it)
                }
    }

    fun queryAll(result: (list: MutableList<Tourist>) -> Unit) {
        LitePal.findAllAsync(Tourist::class.java)
                .listen {
                    result(it)
                }
    }

    fun query(guideName: String, date: String, result: (list: MutableList<Tourist>) -> Unit) {
        LitePal.where("addTime LIKE '%$date%' AND guideName LIKE '%$guideName%'").findAsync(Tourist::class.java).listen {
            result(it)
        }
    }
}