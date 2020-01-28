package com.example.memorandum

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ShortBuffer

class MainActivity : AppCompatActivity() {

    var noteList:MutableList<Note> = ArrayList()

    var masterSqlite = MasterSqlite(this,5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        initNotes()//:FruitAdapter
        var layoutManager= LinearLayoutManager(this)
        recycle.layoutManager=layoutManager


        var adapter= NoteAdapter(this,
            noteList
        )
        /*var lv:ListView = findViewById(R.id.list_view) as ListView
        lv.adapter=adapter*/
        recycle.adapter=adapter


        floatAddBtn.setOnClickListener{
            Toast.makeText(this,"hahah",Toast.LENGTH_SHORT).show()
            var intent = Intent()
            intent.setClass(this,NewActivity::class.java)
            startActivityForResult(intent,1)
            //startActivity(intent)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            1 -> {
                if (resultCode == RESULT_OK) {
                    var returnedData = data?.getStringExtra("data_return")
                    Log.d("tag",returnedData.toString())
                    //这一句要改成存入数据库啦
                    var temp = Note(returnedData.toString())
                    masterSqlite.addData(temp)
                }
            }
        }

    }

    //这里改成从数据库中读取
    fun initNotes(){

        var cursor = masterSqlite.findAllData()

        if(cursor.moveToFirst())
        {
            do {
                var words = cursor.getString(cursor.getColumnIndex("words"))
                var time = cursor.getString(cursor.getColumnIndex("time"))
                Log.d("tag","book author is"+ words)
                var note1 = Note(words)
                note1.time =time
                noteList.add(note1)
            }while (cursor.moveToNext())
        }
        cursor.close()

       /* var i=0
        var j=2
        while (i<=j) {
            var note1 = Note("lalal拉开了看到")
            noteList.add(note1)
            var note2 = Note("哦大家记得发plllllllllllllllllll发即可打开的艰苦打看到")
            noteList.add(note2)

            i++
        }*/
    }
}
