package com.example.memorandum

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

class MasterSqlite(var context: Context,var version:Int) {

    init {
        Log.d("tag","会走到MasterSqlite")
    }

    var dbHelper = MyDatabaseHelper(context,"TestDatabase.db",null,3)

    fun addData(note: Note)
    {
        var db = dbHelper.readableDatabase
        var values = ContentValues()
        values.put("words",note.words)
        db.insert("Note",null,values)
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
        var cursor = db.query("Note",null,null,null,null,null,null)
        return cursor
    }

    }
