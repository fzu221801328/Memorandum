package com.example.memorandum

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

class MasterSqlite(var context: Context,var version:Int) {

    val TABLE_NAME1 = "NoteTime"
    val TABLE_NAME2 = "DeletedNote"

    init {
        Log.d("tag","会走到MasterSqlite")
    }
//更新表时版本要+++
    var dbHelper = MyDatabaseHelper(context,"Database.db",null,8)

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
        values.put("time",DateUtil.nowDateTime)//为什么加上这句话直接就存不进去了//表没更新
        //第一个参数是表名
        db.insert("${TABLE_NAME1}",null,values)
        //Log.d("tag","成功insert")

    }


    /*根据id来更新笔记*/
    fun updateData(note:Note)
    {
        //先通过id查到这条笔记
        var db = dbHelper.readableDatabase
        var values = ContentValues()

        values.put("words",note.words)
        values.put("time",DateUtil.nowDateTime)

        db.update("${TABLE_NAME1}",values,"_id = ?", arrayOf(note.id.toString()))
        Log.d("tag","更新了笔记")
    }



    fun deleteNote(note:Note)
    {
        //先通过id查到这条笔记
        var db = dbHelper.writableDatabase

        db.delete("${TABLE_NAME1}","_id=?", arrayOf(note.id.toString()))
   //还要把这条加到回收站里面

       var values = ContentValues()
        values.put("words",note.words)
        values.put("time",note.time)//为什么加上这句话直接就存不进去了//表没更新
        //第一个参数是表名
        db.insert("${TABLE_NAME2}",null,values)
    }

    /*从回收站恢复一条笔记*/
    fun recover(note:Note)
    {
        //从回收站删除，再添加回原来的地方
        //先通过id查到这条笔记
        var db = dbHelper.writableDatabase

        db.delete("${TABLE_NAME2}","_id=?", arrayOf(note.id.toString()))
        //还要把这条加到回收站里面

        var values = ContentValues()
        values.put("words",note.words)
        values.put("time",note.time)//为什么加上这句话直接就存不进去了//表没更新
        //第一个参数是表名
        db.insert("${TABLE_NAME1}",null,values)
    }

    fun deleteNote2(note:Note)
    {
        //先通过id查到这条笔记
        var db = dbHelper.writableDatabase

        db.delete("${TABLE_NAME2}","_id=?", arrayOf(note.id.toString()))
    }

    fun deleteAll()
    {
        var db = dbHelper.writableDatabase

        db.delete("${TABLE_NAME1}",null, null)

    }

    fun copy()
    {
        var db = dbHelper.writableDatabase

        db.execSQL("insert into ${TABLE_NAME2} select  * from ${TABLE_NAME1}")
    }




    fun findAllData():MutableList<Note>
    {
        var db = dbHelper.writableDatabase
        var cursor = db.query("${TABLE_NAME1}",null,null,null,null,null,null)

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

    fun findAllData2():MutableList<Note>
    {
        var db = dbHelper.writableDatabase
        var cursor = db.query("${TABLE_NAME2}",null,null,null,null,null,null)

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
    }

    }
