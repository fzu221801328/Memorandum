package com.example.memorandum

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.note.*

class NewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note)

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