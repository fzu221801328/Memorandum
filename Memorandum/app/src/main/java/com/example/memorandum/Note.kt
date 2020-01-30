package com.example.memorandum

class Note(var words:String) {
    var time:String = "q"
    //添加一些字段
    var id:Int = 0
    var title:String = ""

    var mode:Int = 1//操作的模式1为新增，2为修改
}