package com.example.memorandum

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val context: Context, private var mnoteList:MutableList<Note>) :

    RecyclerView.Adapter<NoteAdapter.ViewHolder>(),Filterable {


    private var backList //用来备份原始数据
            : MutableList<Note>? = null

    private var mFilter: Myfilter = Myfilter()

    init {
        backList = mnoteList
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        lateinit var note: Note
        var noteName: TextView
        var noteTime: TextView

        init {
            this.noteName = view.findViewById(R.id.itemText)
            this.noteTime = view.findViewById(R.id.itemTime)
            view.setOnClickListener(this)
        }

        //绑定
        fun bind(note: Note) {
            //得到了我们点击的Note，有id，words，time等
            this.note = note
            noteName.setText(note.words)
            noteTime.setText(note.time)
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

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //val note = mnoteList[position]
        holder.bind(mnoteList[position])

    }


    override fun getItemCount(): Int {

        return mnoteList.size

    }


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {

        val view = LayoutInflater.from(context)

            .inflate(R.layout.note_item, parent, false)

        return ViewHolder(view)

    }

    /*override fun getFilter(): Filter {

    }*/

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
}
