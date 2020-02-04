package com.example.memorandum

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class DeletedNoteAdapter(private val context: Context, private var mnoteList:MutableList<Note>) :

    RecyclerView.Adapter<DeletedNoteAdapter.ViewHolder>() {

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {

        lateinit var note:Note
        var noteName: TextView
        var noteTime: TextView

        init {
            this.noteName = view.findViewById(R.id.itemText)
            this.noteTime = view.findViewById(R.id.itemTime)
            view.setOnClickListener(this)
        }

        //绑定
        fun bind(note: Note)
        {
            //得到了我们点击的Note，有id，words，time等
            this.note = note
            noteName.setText(note.words)
            noteTime.setText(note.time)
        }


        override fun onClick(v: View?) {
            val context = v?.context as Activity
            //Toast.makeText(context,noteName.text.toString(),Toast.LENGTH_SHORT).show()

            var intent = Intent(context,DeletedDetailActivity::class.java)
            //Toast.makeText(context,note.id.toString(),Toast.LENGTH_SHORT).show()

            //这边跳去修改,传数据给下一个活动
            intent.putExtra("_id",note.id)
            Log.d("tag","note.id = "+ note.id)
            intent.putExtra("words",note.words)
            Log.d("tag","note.words = "+ note.words)
            intent.putExtra("time",note.time)
            intent.putExtra("mode",2)//2是修改

            context.startActivityForResult(intent,2)

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

}