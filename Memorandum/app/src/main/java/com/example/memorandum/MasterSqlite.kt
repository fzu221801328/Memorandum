package com.example.memorandum

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

class MasterSqlite(var context: Context,var version:Int) {

    init {
        Log.d("tag","会走到MasterSqlite")
    }
//更新表时版本要+++
    var dbHelper = MyDatabaseHelper(context,"Database.db",null,6)

    fun addData(note: Note)
    {
        var db = dbHelper.readableDatabase
        var values = ContentValues()
        values.put("words",note.words)
        Log.d("tag","成功put words")
        values.put("time",DateUtil.nowDateTime)//为什么加上这句话直接就存不进去了
        Log.d("tag","成功put time")
        //第一个参数是表名
        db.insert("NoteTime",null,values)
        Log.d("tag","成功insert")

    }


  /*  fun deleteData(note: Note)
    {
        var db = dbHelper.readableDatabase
        var values = ContentValues()
        values.put("author","maya")
        db.update("Book",values,"author = ?", arrayOf("tun"))
    }

    deleteeee.setOnClickListener {
        var db = dbHelper.writableDatabase
        db.delete("Book","author=?", arrayOf("pan zi"))
    }
*/
    fun findAllData():Cursor
    {
        var db = dbHelper.writableDatabase
        var cursor = db.query("NoteTime",null,null,null,null,null,null)
        return cursor
    }

    }
