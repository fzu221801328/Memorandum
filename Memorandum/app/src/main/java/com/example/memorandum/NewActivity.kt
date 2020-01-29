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
        var time = intent.getStringExtra("time")
        var words = intent.getStringExtra("words")

        editNote.setText(words)
        editTime.setText(time)


        /*var intent = Intent()
        intent.putExtra("data_return",editNote.text.toString())
        setResult(Activity.RESULT_OK,intent)//返回了这个intent*/
        //finish()不能写在这
    }

    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra("data_return",editNote.text.toString())
        setResult(RESULT_OK,intent)//返回了这个intent
        finish()
    }
}