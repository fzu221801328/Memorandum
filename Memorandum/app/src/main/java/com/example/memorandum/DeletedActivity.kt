package com.example.memorandum

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorandum.R.drawable
import com.example.memorandum.R.drawable.ic_dehaze_black_24dp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.recycle
import kotlinx.android.synthetic.main.deleted.*
import kotlinx.android.synthetic.main.note.*
import java.nio.ShortBuffer

class DeletedActivity : AppCompatActivity() {

    var noteList:MutableList<Note> = ArrayList()

    var masterSqlite = MasterSqlite(this,8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deleted)

        setSupportActionBar(deletedToolbar)//替换
        //设置左边那个图标
        deletedToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        deletedToolbar.setNavigationOnClickListener {
            finish()
        }

        initNotes()
        var layoutManager= LinearLayoutManager(this)
        deletedRecycle.layoutManager=layoutManager


        var adapter= DeletedNoteAdapter(this,
            noteList
        )
        deletedRecycle.adapter=adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var returnedMode = data?.getIntExtra("mode",-1)
        when(returnedMode)
        {
            1 -> {
                Log.d("tag","requestCode = 1")
                if (resultCode == RESULT_OK) {
                    var returnedData = data?.getStringExtra("data_return")
                    //Log.d("tag",returnedData.toString())
                    //这一句要改成存入数据库啦
                    var temp = Note(returnedData.toString())
                    masterSqlite.addData(temp)
                    refreshRecyclerView()
                }
            }
            2 ->{//修改
                if (resultCode == RESULT_OK) {
                    Log.d("tag","requestCode = 2")

                    var id = data?.getIntExtra("_id",0)
                    var words = data?.getStringExtra("words")
                    //Log.d("tag","id = "+ id)
                    //Log.d("tag","words = "+ words)
                    var temp = Note(words.toString())
                    temp.id = id!!

                    masterSqlite.updateData(temp)
                    refreshRecyclerView()
                }
            }
            3 ->if (resultCode == RESULT_OK) {
                Log.d("tag","requestCode = 3")
                var id = data?.getIntExtra("_id",0)

                var temp = Note("0")
                temp.id = id!!

                masterSqlite.deleteNote(temp)
                refreshRecyclerView()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deleted_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


    /*更改笔记后更新笔记列表*/
    fun refreshRecyclerView()
    {
        masterSqlite.open()
        if(noteList.size > 0) noteList.clear()
        noteList.addAll(masterSqlite.findAllData())
        masterSqlite.close()
        deletedRecycle.adapter?.notifyDataSetChanged()
    }

    //这里改成从数据库中读取
    fun initNotes(){

        var noteList: MutableList<Note> = masterSqlite.findAllData2()
        this.noteList = noteList

    }

}
