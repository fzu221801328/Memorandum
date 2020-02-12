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
        var i = 0
        //while (i < 15)
        //{
            db.insert("${TABLE_NAME1}",null,values)
          //  i++
        //}

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

    fun queryNote(id:Int):Note
    {
        var note = Note("")
        note.id = id
        var db = dbHelper.writableDatabase

        var cursor = db.query("${TABLE_NAME1}",null,"_id = ?", arrayOf(id.toString()),null,null,null)

        if(cursor.moveToFirst())
        {
                note.words = cursor.getString(cursor.getColumnIndex("words"))
                note.time = cursor.getString(cursor.getColumnIndex("time"))

        }
        cursor.close()

        //db.delete("${TABLE_NAME1}","_id=?", arrayOf(id.toString()))
        return note
    }

    fun queryNote2(id:Int):Note
    {
        var note = Note("")
        note.id = id
        var db = dbHelper.writableDatabase

        var cursor = db.query("${TABLE_NAME2}",null,"_id = ?", arrayOf(id.toString()),null,null,null)

        if(cursor.moveToFirst())
        {
            note.words = cursor.getString(cursor.getColumnIndex("words"))
            note.time = cursor.getString(cursor.getColumnIndex("time"))

        }
        cursor.close()

        //db.delete("${TABLE_NAME1}","_id=?", arrayOf(id.toString()))
        return note
    }

    fun deleteNote(note:Note)
    {
        //先通过id查到这条笔记
        var db = dbHelper.writableDatabase

        var note2:Note = queryNote(note.id)//比一下他们两个一不一样,一样的

        /*Log.d("tag","note.words = "+ note.words)
        Log.d("tag","note2.words = "+ note2.words)
        Log.d("tag","note.time = "+ note.time)
        Log.d("tag","note2.time = "+ note2.time)*/

        db.delete("${TABLE_NAME1}","_id=?", arrayOf(note.id.toString()))
   //还要把这条加到回收站里面

       var values = ContentValues()
        values.put("words",note2.words)
        values.put("time",note2.time)//为什么加上这句话直接就存不进去了//表没更新
        //第一个参数是表名
        db.insert("${TABLE_NAME2}",null,values)
    }

    /*从回收站恢复一条笔记*/
    fun recover(note:Note)
    {
        //从回收站删除，再添加回原来的地方
        //先通过id查到这条笔记
        var db = dbHelper.writableDatabase
        var note2:Note = queryNote2(note.id)

        db.delete("${TABLE_NAME2}","_id=?", arrayOf(note.id.toString()))
        //还要把这条加到回收站里面

        var values = ContentValues()
        values.put("words",note2.words)
        values.put("time",note2.time)//为什么加上这句话直接就存不进去了//表没更新
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

    fun deleteAll2()
    {
        var db = dbHelper.writableDatabase

        db.delete("${TABLE_NAME2}",null, null)
    }

    fun copy()
    {
        var db = dbHelper.writableDatabase
        Log.d("tag","copy")

        //db.execSQL("insert into ${TABLE_NAME2} select  * from ${TABLE_NAME1}")
        //如果主键相同，就不行
        db.execSQL("insert into ${TABLE_NAME2} (words,time)"+ " select words,time from ${TABLE_NAME1}")
        Log.d("tag","copyno")
    }

    fun copy2()
    {
        var db = dbHelper.writableDatabase

        db.execSQL("insert into ${TABLE_NAME1} (words,time)"+ " select words,time from ${TABLE_NAME2}")
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
                //Log.d("tag","book author is"+ words)
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
                //Log.d("tag","book author is"+ words)
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
