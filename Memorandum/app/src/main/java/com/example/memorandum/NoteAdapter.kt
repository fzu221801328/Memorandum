package com.example.memorandum

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NoteAdapter(private val context: Context, private var mnoteList:MutableList<Note>) :

    RecyclerView.Adapter<NoteAdapter.ViewHolder>(),Filterable{

    var flag = 0//是否显示checkbox

    //弄成一个？
    var masterSqlite = MasterSqlite(context,5)


    private var backList //用来备份原始数据
            : MutableList<Note>? = null

    private var mFilter: Myfilter = Myfilter()

    var mSelectedPositions = SparseBooleanArray()//是一个存布尔值的pair
    // var checkStatus:MutableMap<Int, Boolean> = mutableMapOf()//用来记录所有checkbox的状态

    init {
        backList = mnoteList

        ;//在哪里改的？
        var i = 0
        while( i < mnoteList.size ) {
            mSelectedPositions.put(i, false);// 默认所有的checkbox都是没选中
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




        inner class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)
        , View.OnClickListener,View.OnLongClickListener {

        lateinit var note: Note
        var noteName: TextView
        var noteTime: TextView
        var noteSelected: CheckBox

        init {
            this.noteName = view.findViewById(R.id.itemText)
            this.noteTime = view.findViewById(R.id.itemTime)
            this.noteSelected = view.findViewById(R.id.item_checkBox)
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            setIsRecyclable(false)



        }

        //绑定
        fun bind(note: Note) {
            //得到了我们点击的Note，有id，words，time等
            this.note = note
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

            var intent = Intent(context, NewActivity::class.java)
            //Toast.makeText(context,note.id.toString(),Toast.LENGTH_SHORT).show()

            //这边跳去修改,传数据给下一个活动
            intent.putExtra("_id", note.id)
            Log.d("tag", "note.id = " + note.id)
            intent.putExtra("words", note.words)
            Log.d("tag", "note.words = " + note.words)
            intent.putExtra("time", note.time)
            intent.putExtra("mode", 2)//2是修改

            context.startActivityForResult(intent, 2)

        }

        @SuppressLint("RestrictedApi")
        override fun onLongClick(view: View?): Boolean {
            Toast.makeText(context, "长按了", Toast.LENGTH_SHORT).show()

            /*隐藏一些，显示一些*/
            var ac = context as Activity
            var deletebutton = ac.findViewById<Button>(R.id.delete_button)
            var floatbutton = ac.findViewById<FloatingActionButton>(R.id.floatAddBtn)

            deletebutton.setOnClickListener {
                var list = getSelectedItem()
                var note = Note("")
                list.forEach{
                    note.id = it
                    masterSqlite.deleteNote(note)

                    /*mSelectedPositions.clear()
                    var i = 0
                    while (i < mnoteList.size) {
                            mSelectedPositions.
                                add(false);

                        i++
                    }*/
                    //mSelectedPositions.clear()//?删除完怎么把选择框清掉
                    refreshRecyclerView()

                }

            }

            if(flag == 0)
            {
                //noteSelected.visibility = View.VISIBLE
                flag = 1
                deletebutton?.visibility = View.VISIBLE
                floatbutton.visibility = View.INVISIBLE


                Log.d("tag","floatbutton是否显示"+ View.INVISIBLE.toString())
                notifyDataSetChanged()
            }
            else
            {
                flag = 0

                deletebutton?.visibility = View.INVISIBLE
                floatbutton.visibility = View.VISIBLE
                notifyDataSetChanged()
            }

            refreshRecyclerView()//不保留打勾
            return true

        }


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //val note = mnoteList[position]
        holder.bind(mnoteList[position])

        holder.noteSelected.setOnCheckedChangeListener(null);//清掉监听器
        holder.noteSelected.setChecked(mSelectedPositions.get(position)!!);//设置选中状态

        holder.noteSelected.setOnClickListener {

            if (isItemChecked(position)) {
                setItemChecked(position, false);
                Toast.makeText(context,"不选"+mnoteList[position].words,Toast.LENGTH_SHORT).show()
            } else {
                setItemChecked(position, true);
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
                selectList.add(mnoteList[i].id);
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


    //加上inner内部类，可使用外部类的变量
    inner class Myfilter : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            var result = FilterResults()
            var list: MutableList<Note>?

            Log.d("tag",charSequence.toString()+"在Myfilter里")
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList
            } else {//否则把符合条件的数据对象添加到集合中
                list = ArrayList()
                backList?.forEach { note ->
                    Log.d("tag","进了多少次foreach")
                    if (note.words.contains(charSequence!!)) {
                        list.add(note)

                    }
                }

            }
            result.values = list //将得到的集合保存到FilterResults的value变量中
            result.count = list?.size!! //将集合的大小保存到FilterResults的count变量中

            Log.d("tag","result.count = "+result.count)

            //问题是结果如何反应到列表里
                    return result
        }



        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            mnoteList = (filterResults?.values as MutableList<Note>?)!!
            if (filterResults?.count!! >= 0) {

                notifyDataSetChanged() //通知数据发生了改变
                Log.d("tag","通知数据发生了改变")
            } else {
                // notifyDataSetInvalidated() //通知数据失效
            }
        }

    }

    override fun getFilter(): Filter {

        if (mFilter == null)
            mFilter = Myfilter()
        
        return mFilter

    }


    /*更改笔记后更新笔记列表*/
    fun refreshRecyclerView()
    {
        //flag = 0
        masterSqlite.open()
        if(mnoteList.size > 0) mnoteList.clear()
        mnoteList.addAll(masterSqlite.findAllData())
        masterSqlite.close()

        //不保留打勾
        mSelectedPositions.clear()
        var i = 0
        while( i < mnoteList.size ) {
            mSelectedPositions.put(i, false);// 默认所有的checkbox都是没选中
            i++
        }

        notifyDataSetChanged()
    }

}

