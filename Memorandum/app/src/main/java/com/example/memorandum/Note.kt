package com.example.memorandum

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Note(var words:String) {


    var time:String = "q"
    //添加一些字段
    var id:Int = 0
    var title:String = ""

    var mode:Int = 1//操作的模式1为新增，2为修改，3为删除


    /*字符串转换成日期*/
    fun translateTime(dtStart:String):Date
    {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
       // try {
            val date: Date = format.parse(dtStart)
        /*    System.out.println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }*/
        return date
    }
}