package com.example.memorandum

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class MyDatabaseHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    init {
        Log.d("tag","会走到MyDatabaseHelper")
    }

    val TABLE_NAME1 = "NoteTime"
    val TABLE_NAME2 = "DeletedNote"

//建立一个新的表
    private var CREATE_NOTE = "create table ${TABLE_NAME1} ("+
            "_id integer primary key autoincrement,"+
            "words text,"+"time text,"+"location text)"

    //回收站
    private  var CREATE_DELETED_NOTE = "create table ${TABLE_NAME2}("+
            "_id integer primary key autoincrement,"+
            "words text,"+"time text,"+"location text)"

    private var mContext=context

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_NOTE)
        db?.execSQL(CREATE_DELETED_NOTE)
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("drop table if exists ${TABLE_NAME1}")
        db?.execSQL("drop table if exists ${TABLE_NAME2}")

        onCreate(db)
    }

}