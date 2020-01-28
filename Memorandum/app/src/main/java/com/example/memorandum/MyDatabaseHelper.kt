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

    private var CREATE_NOTE = "create table Note ("+
            "_id integer primary key autoincrement,"+
            "words text)"

   /* private  var CREATE_CATEGORY = "create table Category("+
            "id integer primary key autoincrement,"+
            "category_name text,"+
            "category_code integer)"*/

    private var mContext=context;

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_NOTE)
      //  db?.execSQL(CREATE_CATEGORY)
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Book")
        db?.execSQL("drop table if exists Category")
        onCreate(db)
    }
}