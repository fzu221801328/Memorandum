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
                }
            }
        }

    }

    fun initNotes(){
        var i=0
        var j=2
        while (i<=j) {
            var note1 = Note("lalal拉开了看到")
            noteList.add(note1)
            var note2 = Note("哦大家记得发plllllllllllllllllll发即可打开的艰苦打看到")
            noteList.add(note2)

            i++
        }
    }
}
