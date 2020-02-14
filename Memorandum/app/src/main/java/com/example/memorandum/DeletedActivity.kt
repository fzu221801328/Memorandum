package com.example.memorandum

import android.annotation.SuppressLint
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
import android.widget.Button
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

    var masterSqlite = MasterSqlite(this)

    var adapter = DeletedNoteAdapter(this,noteList)

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


        adapter= DeletedNoteAdapter(this,
            noteList
        )
        deletedRecycle.adapter=adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var returnedMode = data?.getIntExtra("mode",-1)
        when(returnedMode)
        {
            5 ->{//恢复
                if (resultCode == RESULT_OK) {
                   // Log.d("tag","requestCode = 2")

                    var id = data?.getIntExtra("_id",0)
                    var title = data?.getStringExtra("title")
                    var words = data?.getStringExtra("words")
                    var time = data?.getStringExtra("time")
                    //Log.d("tag","id = "+ id)
                    //Log.d("tag","words = "+ words)
                    var temp = Note(words.toString())
                    temp.id = id!!
                    temp.time = time!!
                    temp.title = title!!

                    masterSqlite.recover(temp)
                    refreshRecyclerView()
                }
            }
            4 -> {//彻底删除
                if (resultCode == RESULT_OK) {
                    //Log.d("tag","requestCode = 3")
                    var id = data?.getIntExtra("_id", 0)

                    var temp = Note("0")
                    temp.id = id!!

                    masterSqlite.deleteNote2(temp)
                    refreshRecyclerView()
                }
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
        noteList.addAll(masterSqlite.findAllData(2))
        masterSqlite.close()
        deletedRecycle.adapter?.notifyDataSetChanged()
    }

    //这里改成从数据库中读取
    fun initNotes(){

        var noteList: MutableList<Note> = masterSqlite.findAllData(2)
        this.noteList = noteList

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.deleted_clear ->{
                AlertDialog.Builder(this)
                    .setMessage("你确定要彻底删除全部笔记吗？")
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            masterSqlite.deleteAll2()
                            refreshRecyclerView()
                        }).setNegativeButton(android.R.string.no,
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).create().show()

            }
            R.id.deleted_replay ->{
                AlertDialog.Builder(this)
                    .setMessage("你要恢复全部笔记吗？")
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            masterSqlite.copy2()
                            masterSqlite.deleteAll2()
                            refreshRecyclerView()
                        }).setNegativeButton(android.R.string.no,
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).create().show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        var deletebutton = findViewById<Button>(R.id.delete_forever_button)
        var recoverbutton = findViewById<Button>(R.id.recover_button)

        if(deletebutton.visibility == View.VISIBLE ||
            recoverbutton.visibility == View.VISIBLE  ) {

            recoverbutton.visibility = View.INVISIBLE
            deletebutton.visibility = View.INVISIBLE

            //要是同一个adapter
            adapter.flag = 0//去掉多选框
            adapter.refreshRecyclerView()

            Log.d("tag","flag = "+ adapter.flag)
        }
        else
            super.onBackPressed()
    }
}
