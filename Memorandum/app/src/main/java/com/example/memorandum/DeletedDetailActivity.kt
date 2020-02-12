package com.example.memorandum

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.deleted_detail.*
import kotlinx.android.synthetic.main.note.*

import kotlinx.android.synthetic.main.note_item.*

class DeletedDetailActivity: AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deleted_detail)//之前这边忘记改了

        setSupportActionBar(deletedDetailToolbar)

        deletedDetailToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        Log.d("tag","成成成成功跳进去deleteddetail")

        //直接就不要了那个view.onclicklistener（）？？
        deletedDetailToolbar.setNavigationOnClickListener {

            finish()
        }
        //接受来自上一个活动的数据
        // var intent = intent
        var id =intent.getIntExtra("id",0)
        //显示修改之前的这些

        var oldTime = intent.getStringExtra("time")
        var oldWords = intent.getStringExtra("words")
        var location = intent.getStringExtra("location")

        deletedNoteNote.setText(oldWords)
        deletedNoteTime.setText(oldTime)
        deleted_location.setText(location)
    }

    override fun onBackPressed() {

        finish()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deleted_detail_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var mode =intent.getIntExtra("mode",-1)
        var id =intent.getIntExtra("_id",0)
        var oldTime = intent.getStringExtra("time")
        var oldWords = intent.getStringExtra("words")

        var intent2 = Intent()


        when(item.itemId){
            R.id.menu_delete_forever->{
                AlertDialog.Builder(this)
                    .setMessage("你确定要彻底删除该笔记吗？")
                    .setPositiveButton(android.R.string.yes,DialogInterface.OnClickListener { dialog, which ->

                            intent2.putExtra("mode",4)//4回收站删除
                            intent2.putExtra("time",oldTime)
                            intent2.putExtra("words",oldWords)
                            intent2.putExtra("_id",id)

                        setResult(RESULT_OK,intent2)//返回了这个intent
                        finish()
                    }).setNegativeButton(android.R.string.no,DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).create().show()

            }
            R.id.menu_replay ->{
                AlertDialog.Builder(this)
                    .setMessage("你确定要恢复该笔记吗？")
                    .setPositiveButton(android.R.string.yes,DialogInterface.OnClickListener { dialog, which ->

                        intent2.putExtra("mode",5)//5回收站恢复
                        intent2.putExtra("time",oldTime)
                        intent2.putExtra("words",oldWords)
                        intent2.putExtra("_id",id)

                        setResult(RESULT_OK,intent2)//返回了这个intent
                        finish()
                    }).setNegativeButton(android.R.string.no,DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).create().show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

}