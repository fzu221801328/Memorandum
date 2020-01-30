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

    fun open()
    {
        dbHelper.writableDatabase
    }

    fun close()
    {
        dbHelper.close()
    }

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

    /*根据id来更新笔记*/
    fun updateData(note:Note)
    {
        //先通过id查到这条笔记
        var db = dbHelper.readableDatabase
        var values = ContentValues()

        values.put("words",note.words)
        values.put("time",DateUtil.nowDateTime)

        db.update("NoteTime",values,"_id = ?", arrayOf(note.id.toString()))
        Log.d("tag","更新了笔记")
    }




   /* deleteeee.setOnClickListener {
        var db = dbHelper.writableDatabase
        db.delete("Book","author=?", arrayOf("pan zi"))
    }
*/
    fun findAllData():MutableList<Note>
    {
        var db = dbHelper.writableDatabase
        var cursor = db.query("NoteTime",null,null,null,null,null,null)

        var noteList:MutableList<Note> = ArrayList()
       // var cursor = masterSqlite.findAllData()

        if(cursor.moveToFirst())
        {
            do {
                var words = cursor.getString(cursor.getColumnIndex("words"))
                var time = cursor.getString(cursor.getColumnIndex("time"))
                var id = cursor.getString(cursor.getColumnIndex("_id"))
                Log.d("tag","book author is"+ words)
                var note1 = Note(words)
                note1.time =time
                note1.id = id.toInt()
                noteList.add(note1)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return noteList
       // return cursor
    }

    }
