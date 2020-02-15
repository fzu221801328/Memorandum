package com.example.memorandum

import android.annotation.TargetApi
import android.app.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.note.*

import kotlinx.android.synthetic.main.note_item.*
import java.text.Normalizer
import java.util.*

@Suppress("DEPRECATION")
class NewActivity: AppCompatActivity() {

    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mHour = 0
    var mMinute = 0

    val DATE_DIALOG = 1
    val TIME_DIALOG = 2

    var PERMISSION_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note)

        setSupportActionBar(editToolbar)

        editToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        //直接就不要了那个view.onclicklistener（）？？
        editToolbar.setNavigationOnClickListener {

            saveNote()
        }
        //接受来自上一个活动的数据
       // var intent = intent
        //var id =intent.getIntExtra("id",0)
        //显示修改之前的这些

        var mode =intent.getIntExtra("mode",-1)

        var oldTime = intent.getStringExtra("time")
        var oldTitle = intent.getStringExtra("title")
        var oldWords = intent.getStringExtra("words")
        var oldLocation = intent.getStringExtra("location")

        editTitle.setText(oldTitle)
        editNote.setText(oldWords)
        if(mode != 1)//光标移到最后
        {
            if(oldTitle.isNotEmpty())
                editTitle.setSelection(oldTitle.length)
            if(oldWords.isNotEmpty())
                editNote.setSelection(oldWords.length)
        }
        editTime.setText(oldTime)
        edit_location.setText(oldLocation)



        val ca = Calendar.getInstance()
        mYear = ca[Calendar.YEAR]
        mMonth = ca[Calendar.MONTH]
        mDay = ca[Calendar.DAY_OF_MONTH]
        mHour = ca[Calendar.HOUR_OF_DAY]
        mMinute = ca[Calendar.MINUTE]

    }

    override fun onBackPressed() {

        saveNote()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var mode =intent.getIntExtra("mode",-1)
        var id =intent.getIntExtra("_id",0)
        var oldTime = intent.getStringExtra("time")
        var oldWords = intent.getStringExtra("words")

        var intent2 = Intent()



        when(item.itemId){
            R.id.menu_delete ->{
                AlertDialog.Builder(this)
                    .setMessage("你确定要删除该笔记吗？")
                    .setPositiveButton(android.R.string.yes,DialogInterface.OnClickListener { dialog, which ->
                        if(mode == 1)//新增笔记，就什么也不干
                        {
                            intent2.putExtra("mode",-1)
                        }
                        else{
                            intent2.putExtra("mode",3)
                            intent2.putExtra("time",oldTime)
                            intent2.putExtra("words",oldWords)
                            intent2.putExtra("_id",id)
                        }
                        setResult(RESULT_OK,intent2)//返回了这个intent
                        finish()
                    }).setNegativeButton(android.R.string.no,DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).create().show()

            }
            R.id.menu_alarm ->{
                //日历相关
                checkCalendarPermission()


                dateDisplay.visibility = View.VISIBLE
                timeDisplay.visibility = View.VISIBLE

                showDialog(DATE_DIALOG)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*保存笔记*/
    fun saveNote(){
        //要通过intent传过来的mode来选择，是新建还是修改什么的
        var mode =intent.getIntExtra("mode",-1)
        //-1应该是出错了不用干什么
        if(mode == 1)//新增笔记
        {
            var intent2 = Intent()
            //如果新增笔记words或title为空，就不添加它了
            if(editTitle.text.toString() == "" || editNote.text.toString() == "")
                intent2.putExtra("mode",-1)
            else
            {
                intent2.putExtra("title",editTitle.text.toString())
                intent2.putExtra("words",editNote.text.toString())
                intent2.putExtra("mode",1)
            }

            setResult(RESULT_OK,intent2)//返回了这个intent
            finish()//不能在oncreate里写
        }
        else if(mode == 2)//修改笔记
        {
            var intent2 = Intent()

            var id =intent.getIntExtra("_id",0)

            //如果点进去没修改title和words，就不更新时间
            var oldTitle = intent.getStringExtra("title")
            var oldWords = intent.getStringExtra("words")

            if(editTitle.text.toString() == oldTitle && editNote.text.toString() == oldWords)
                intent2.putExtra("mode",-1)
            else
            {
                intent2.putExtra("_id",id)
                intent2.putExtra("time",editTime.text.toString())
                intent2.putExtra("title",editTitle.text.toString())
                intent2.putExtra("words",editNote.text.toString())
                intent2.putExtra("mode",2)
            }

            setResult(RESULT_OK,intent2)//返回了这个intent
            finish()
        }

    }


/*************************************************************************************/
/*********************往下是 提醒 和 日历************************************************/

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(id: Int): Dialog? {
        when (id) {

            DATE_DIALOG -> return DatePickerDialog(this,
                DatePickerDialog.THEME_HOLO_LIGHT,mdateListener, mYear, mMonth, mDay)

            TIME_DIALOG -> return TimePickerDialog(this,
                TimePickerDialog.THEME_HOLO_LIGHT,mtimeListener,mHour,mMinute,false)
            //DatePickerDialog(this, mdateListener, mYear, mMonth, mDay)
        }
        return null
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    fun displayDate() {
        dateDisplay!!.text = StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" ")
    }

    fun displayTime(){//分钟设置为两位数
        timeDisplay!!.text = StringBuffer().append(mHour).append(":").append(String.format("%02d",mMinute)).append(" ")
    }

    private val mdateListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            displayDate()
            showDialog(TIME_DIALOG)
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private val mtimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        mHour = hourOfDay
        mMinute = minute
        displayTime()

        var c = CalendarReminderUtils(this)
        c.checkAndAddCalendarAccount()

        var note = Note(editNote.text.toString())
        note.title = editTitle.text.toString()
        note.location = edit_location.text.toString()
        c.insert(mYear,mMonth,mDay,mHour,mMinute,note)
        alarmImage.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkCalendarPermission() {
        var readPermission = checkSelfPermission(android.Manifest.permission.READ_CALENDAR)
        var writePermission = checkSelfPermission(android.Manifest.permission.WRITE_CALENDAR)
        if (readPermission == PackageManager.PERMISSION_GRANTED
            && writePermission == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("tag", "有 permission")

        }  //表示有权限
        else {
            Log.d("tag", "require permission")
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_CALENDAR,
                    android.Manifest.permission.WRITE_CALENDAR
                ), PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            //有权限
            Log.d("tag", "有 permission")
        } else {
            finish()
        }
    }

}