package com.example.memorandum

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.note.*
import kotlinx.android.synthetic.main.note_item.*

class NewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note)

        //接受来自上一个活动的数据
        var intent = intent
        //var id =intent.getIntExtra("id",0)
        //显示修改之前的这些
        var oldTime = intent.getStringExtra("time")
        var oldWords = intent.getStringExtra("words")

        editNote.setText(oldWords)
        editTime.setText(oldTime)


    }

    override fun onBackPressed() {



        //要通过intent传过来的mode来选择，是新建还是修改什么的
        var mode =intent.getIntExtra("mode",-1)
        //-1应该是出错了不用干什么
        if(mode == 1)//要修改笔记，传新的值回去了
        {
            var intent2 = Intent()
            intent2.putExtra("data_return",editNote.text.toString())
            setResult(RESULT_OK,intent2)//返回了这个intent
            finish()//不能在oncreate里写
        }
        else if(mode == 2)
        {
            var intent2 = Intent()

            var id =intent.getIntExtra("_id",0)

            intent2.putExtra("_id",id)
            intent2.putExtra("time",editTime.text.toString())
            intent2.putExtra("words",editNote.text.toString())

            setResult(RESULT_OK,intent2)//返回了这个intent
            finish()
        }


    }
}