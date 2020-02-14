package com.example.memorandum

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class DeletedNoteAdapter(private val context: Context, private var mnoteList:MutableList<Note>) :

    RecyclerView.Adapter<DeletedNoteAdapter.ViewHolder>() {

    var flag = 0//是否显示checkbox

    var masterSqlite = MasterSqlite(context)

    var mSelectedPositions = SparseBooleanArray()//是一个存布尔值的pair

    init {
        var i = 0
        while( i < mnoteList.size ) {
            mSelectedPositions.put(i, false)// 默认所有的checkbox都是没选中
            i++
        }
    }

    private fun setItemChecked(position: Int, isChecked: Boolean) {
        mSelectedPositions.put(position, isChecked)
    }

    //根据位置判断条目是否选中
    private fun isItemChecked(position: Int): Boolean {
        return mSelectedPositions[position]
    }

    inner class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener
    ,View.OnLongClickListener{

        lateinit var note:Note
        var noteTitle:TextView
        var noteName: TextView
        var noteTime: TextView
        var noteSelected: CheckBox

        init {
            this.noteTitle = view.findViewById(R.id.itemTitle)
            this.noteName = view.findViewById(R.id.itemText)
            this.noteTime = view.findViewById(R.id.itemTime)
            this.noteSelected = view.findViewById(R.id.item_checkBox)
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        //绑定
        fun bind(note: Note)
        {
            //得到了我们点击的Note，有id，words，time等
            this.note = note
            noteTitle.setText(note.title)
            noteName.setText(note.words)
            noteTime.setText(note.time)

            if(flag == 1)
            {
                noteSelected.visibility = View.VISIBLE
            }
            else
                noteSelected.visibility = View.GONE
        }


        override fun onClick(v: View?) {
            val context = v?.context as Activity
            //Toast.makeText(context,noteName.text.toString(),Toast.LENGTH_SHORT).show()

            var intent = Intent(context,DeletedDetailActivity::class.java)
            //Toast.makeText(context,note.id.toString(),Toast.LENGTH_SHORT).show()

            //这边跳去detail,传数据给下一个活动
            intent.putExtra("_id",note.id)
            intent.putExtra("title", note.title)
            intent.putExtra("words",note.words)
            intent.putExtra("time",note.time)
            intent.putExtra("location",note.location)
            intent.putExtra("mode",2)//2是修改

            context.startActivityForResult(intent,2)

        }

        override fun onLongClick(v: View?): Boolean {
            Toast.makeText(context, "长按了", Toast.LENGTH_SHORT).show()

            /*隐藏一些，显示一些*/
            var ac = context as Activity
            var deletebutton = ac.findViewById<Button>(R.id.delete_forever_button)
            var recoverbutton = ac.findViewById<Button>(R.id.recover_button)

            deletebutton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setMessage("你确定要彻底删除选中的笔记吗？")
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            var list = getSelectedItem()
                            var note = Note("")
                            list.forEach{
                                note.id = it
                                masterSqlite.deleteNote2(note)

                                //删除完把选择框清掉
                                deletebutton?.visibility = View.INVISIBLE
                                recoverbutton.visibility = View.INVISIBLE

                                flag = 0//去掉多选框
                                refreshRecyclerView()

                            }
                        }).setNegativeButton(android.R.string.no,
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).create().show()

            }

            recoverbutton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setMessage("你要恢复选中的笔记吗？")
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            var list = getSelectedItem()
                            var note = Note("")
                            list.forEach{
                                note.id = it
                                masterSqlite.recover(note)

                                //恢复完把选择框清掉
                                deletebutton?.visibility = View.INVISIBLE
                                recoverbutton.visibility = View.INVISIBLE

                                flag = 0//去掉多选框
                                refreshRecyclerView()

                            }
                        }).setNegativeButton(android.R.string.no,
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).create().show()

            }

            if(flag == 0)
            {
                flag = 1
                deletebutton?.visibility = View.VISIBLE
                recoverbutton.visibility = View.VISIBLE

                //Log.d("tag","floatbutton是否显示"+ View.INVISIBLE.toString())
                notifyDataSetChanged()
            }
            else
            {
                flag = 0

                deletebutton?.visibility = View.INVISIBLE
                recoverbutton.visibility = View.INVISIBLE
                notifyDataSetChanged()
            }

            refreshRecyclerView()//不保留打勾
            return true

        }


    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //val note = mnoteList[position]
        holder.bind(mnoteList[position])

        holder.noteSelected.setOnCheckedChangeListener(null)//清掉监听器
        holder.noteSelected.setChecked(mSelectedPositions.get(position)!!)//设置选中状态

        holder.noteSelected.setOnClickListener {

            if (isItemChecked(position)) {
                setItemChecked(position, false)
                Toast.makeText(context,"不选"+mnoteList[position].words,Toast.LENGTH_SHORT).show()
            } else {
                setItemChecked(position, true)
                Toast.makeText(context,"选中"+mnoteList[position].words,Toast.LENGTH_SHORT).show()
            }
        }

    }

    /*获得选中条目的结果*/
    fun  getSelectedItem(): MutableList<Int> {
        var selectList:MutableList<Int> = ArrayList()
        var i = 0
        while (i < mnoteList.size) {
            if (isItemChecked(i)) {
                mSelectedPositions.delete(i)//然后怎么把打勾去掉
                selectList.add(mnoteList[i].id)
            }
            i++
        }
        return selectList
    }



    override fun getItemCount(): Int {

        return mnoteList.size

    }



    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {

        val view = LayoutInflater.from(context)

            .inflate(R.layout.note_item, parent, false)

        return ViewHolder(view)

    }

    /*更改笔记后更新笔记列表*/
    fun refreshRecyclerView()
    {
        //flag = 0
        Log.d("tag","flag = "+ flag)

        masterSqlite.open()
        if(mnoteList.size > 0) mnoteList.clear()
        mnoteList.addAll(masterSqlite.findAllData(2))
        masterSqlite.close()

        //不保留打勾
        mSelectedPositions.clear()
        var i = 0
        while( i < mnoteList.size ) {
            mSelectedPositions.put(i, false)// 默认所有的checkbox都是没选中
            i++
        }
        Log.d("tag","flag = "+ flag)
        notifyDataSetChanged()
    }

}

