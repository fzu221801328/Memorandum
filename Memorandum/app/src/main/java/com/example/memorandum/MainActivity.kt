package com.example.memorandum

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.example.memorandum.R.drawable
import com.example.memorandum.R.drawable.ic_dehaze_black_24dp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ShortBuffer


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    var noteList: MutableList<Note> = ArrayList()

    var masterSqlite = MasterSqlite(this)

    var adapter = NoteAdapter(this, noteList)

    lateinit var mLocationClient: LocationClient

    lateinit var currentLocation: String

    var timeFlag = 0
    var lengthFlag = 0
    var charFlag = 0



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar)//替换
        //设置左边那个图标
        mainToolbar.setNavigationIcon(R.drawable.ic_dehaze_black_24dp)

        mLocationClient = LocationClient(applicationContext)
        mLocationClient.registerLocationListener(MyLocationListener())
        requestPermission()//location相关

        initNotes()
        var layoutManager = LinearLayoutManager(this)
        recycle.layoutManager = layoutManager

        adapter = NoteAdapter(this, noteList)
        recycle.adapter = adapter


        floatAddBtn.setOnClickListener {
           // Toast.makeText(this, "hahah", Toast.LENGTH_SHORT).show()
            var intent = Intent()
            intent.setClass(this, NewActivity::class.java)

            intent.putExtra("time", DateUtil.nowDateTime)
            intent.putExtra("mode", 1)//1是新增
            intent.putExtra("location", currentLocation)

            startActivityForResult(intent, 1)//跳过去是新增

            Log.d("tag", "float跳过去啊")
        }

    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        var deletebutton = findViewById<Button>(R.id.delete_button)
        var floatbutton = findViewById<FloatingActionButton>(R.id.floatAddBtn)


        if (deletebutton?.visibility == View.VISIBLE) {
            deletebutton?.visibility = View.INVISIBLE
            floatbutton.visibility = View.VISIBLE

            adapter.flag = 0//去掉多选框
            adapter.refreshRecyclerView()
        } else
            super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var returnedMode = data?.getIntExtra("mode", -1)
        when (returnedMode) {
            1 -> {
                Log.d("tag", "requestCode = 1")
                if (resultCode == RESULT_OK) {

                    var title = data?.getStringExtra("title")
                    var words = data?.getStringExtra("words")

                    //这一句要改成存入数据库啦
                    var temp = Note(words.toString())
                    temp.title = title.toString()
                    temp.location = currentLocation
                    masterSqlite.addData(temp)
                }
            }
            2 -> {//修改
                if (resultCode == RESULT_OK) {
                    Log.d("tag", "requestCode = 2")

                    var id = data?.getIntExtra("_id", 0)
                    var title = data?.getStringExtra("title")//忘了这个变成Memorandum
                    var words = data?.getStringExtra("words")
                    //Log.d("tag","id = "+ id)
                    //Log.d("tag","words = "+ words)
                    var temp = Note(words.toString())
                    temp.title = title.toString()
                    temp.id = id!!
                    temp.location = currentLocation

                    masterSqlite.updateData(temp)
                }
            }
            3 -> if (resultCode == RESULT_OK) {
                Log.d("tag", "requestCode = 3")
                var id = data?.getIntExtra("_id", 0)
                //var time = data?.getStringExtra("time")
                //var words = data?.getStringExtra("words")
                var temp = Note("0")
                temp.id = id!!
                //删除只需要id
                //temp.time = time!!
                //temp.words = words!!

                masterSqlite.deleteNote(temp)
            }
        }
        refreshRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        var mSearch = menu?.findItem(R.id.menu_search)
        var mSearchView = mSearch?.actionView as SearchView
        mSearchView.queryHint = "Search"

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // 当点击搜索按钮时触发该方法
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // 当搜索内容改变时触发该方法
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                Log.d("tag", newText + "内容改变了")
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    /*更改笔记后更新笔记列表*/
    fun refreshRecyclerView() {
        masterSqlite.open()
        if (noteList.size > 0) noteList.clear()
        noteList.addAll(masterSqlite.findAllData())
        masterSqlite.close()
        //默认按时间排序
        noteList.sortByDescending {  it.translateTime(it.time)  }
        recycle.adapter?.notifyDataSetChanged()
    }

    fun sortRecyclerView(select: Int) {
        masterSqlite.open()
        if (noteList.size > 0) noteList.clear()
        noteList.addAll(masterSqlite.findAllData())

        when (select) {
            1 -> {
                noteList.sortBy {
                    it.words.length
                }
                Log.d("tag", "选了1")
            }
            2 -> {
                noteList.sortByDescending {
                    it.words.length
                }
                Log.d("tag", "选了2")
            }
            3 -> {
                noteList.sortBy {
                    it.translateTime(it.time)
                }
                Log.d("tag", "选了3")
            }
            4 -> {
                noteList.sortByDescending {
                    it.translateTime(it.time)
                }
                Log.d("tag", "选了4")
            }
            5 -> {
                noteList.sortBy {
                    it.title
                }
            }
            6 -> {
                noteList.sortByDescending {
                    it.title
                }
            }
        }

        masterSqlite.close()
        recycle.adapter?.notifyDataSetChanged()
    }

    //这里改成从数据库中读取
    fun initNotes() {

        var noteList: MutableList<Note> = masterSqlite.findAllData()
        //默认按时间排序
        noteList.sortByDescending {  it.translateTime(it.time)  }
        this.noteList = noteList


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent2 = Intent(this, DeletedActivity::class.java)


        when (item.itemId) {
            R.id.menu_recycle -> {
                Log.d("tag","到recycle这里")
                startActivityForResult(intent2, 9)//要forresult才能到那个函数
            }
            R.id.menu_clear -> {
                AlertDialog.Builder(this)
                    .setMessage("你确定要删除全部笔记吗？")
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            Log.d("tag", "哪个不行")
                            masterSqlite.copy()
                            Log.d("tag", "copy不行")
                            masterSqlite.deleteAll()
                            Log.d("tag", "deleteall不行")
                            refreshRecyclerView()
                        }).setNegativeButton(android.R.string.no,
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).create().show()

            }
            R.id.length_sort -> {
                if (lengthFlag % 2 == 0)//是偶数
                    sortRecyclerView(1)
                else
                    sortRecyclerView(2)
                lengthFlag++
            }
            R.id.time_sort -> {
                if (timeFlag % 2 == 0)//是偶数
                    sortRecyclerView(3)
                else
                    sortRecyclerView(4)
                timeFlag++
            }
            R.id.char_sort -> {
                if (charFlag % 2 == 0)//是偶数
                    sortRecyclerView(5)
                else
                    sortRecyclerView(6)
                charFlag++
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun requestPermission() {

        //权限
        var permissionList = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission
                    .ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission
                    .READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE)
        }
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission
                    .WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!permissionList.isEmpty()) {
            var permissions = permissionList.toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, 1)
        } else {
            requestLocation()
        }

    }

    fun requestLocation() {
        initLocation()
        mLocationClient.start()
    }

    fun initLocation() {
        var option = LocationClientOption()
        option.scanSpan = 5000
        option.setIsNeedAddress(true)
        mLocationClient.locOption = option
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient.stop()
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0) {
                    for (result: Int in grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(
                                this, "必须同意所有权限才能使用本程序",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()

                            return
                        }
                    }
                    requestLocation()
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    inner class MyLocationListener : BDLocationListener {

        override fun onReceiveLocation(location: BDLocation?) {
            runOnUiThread(object : Runnable {


                override fun run() {
                    var currentPosition = StringBuilder()
                    //if(location == null)
                    //  positionText.text = null

                    currentPosition.append("纬度：").append(location?.latitude).append("\n")
                    currentPosition.append("经度：").append(location?.longitude).append("\n")
                    currentPosition.append("国家：").append(location?.country).append("\n")
                    currentPosition.append("省：").append(location?.province).append("\n")
                    currentPosition.append("市：").append(location?.city).append("\n")
                    currentPosition.append("区：").append(location?.district).append("\n")
                    currentPosition.append("街道：").append(location?.street).append("\n")
                    /*currentPosition.append("定位方式：")
                    if (location?.locType == BDLocation.TypeGpsLocation) {
                        currentPosition.append("GPS")
                    } else if (location?.locType ==
                        BDLocation.TypeNetWorkLocation
                    ) {
                        currentPosition.append("网络")
                    } else
                        Log.d("tag", "doubushi")*/
                    currentLocation = currentPosition.toString()

                }
            })


        }
    }
}
