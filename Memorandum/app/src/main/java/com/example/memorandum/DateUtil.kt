package com.example.memorandum

import java.text.SimpleDateFormat
import java.util.*


//外部可直接获得属性值
object DateUtil {

    val nowDateTime:String
    get() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date())
    }

    val nowDate:String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(Date())
        }

    val nowTime:String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss")
            return sdf.format(Date())
        }

    val nowTimeDetail:String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss.SSS")
            return sdf.format(Date())
        }

    fun getFormatTime(format:String = ""):String
    {
        val ft = format
        val sdf = if(!ft.isEmpty()) SimpleDateFormat(ft)
                        else SimpleDateFormat("yyyMMddHHmmss")
        return sdf.format(Date())
    }
}