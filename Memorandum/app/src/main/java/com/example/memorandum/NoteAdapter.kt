package com.example.memorandum

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val context: Context, private var mnoteList:MutableList<Note>) :

    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        var noteName: TextView

        init {

            this.noteName = view.findViewById(R.id.itemText)
        }

    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = mnoteList[position]

        holder.noteName.setText(note.words)
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