package com.example.tripreminder2021.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tripreminder2021.R
import com.facebook.internal.Mutable


internal class NotesRecyclerAdapter(private var noteList: MutableList<String>)
                                    :RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val textView=itemView.findViewById(R.id.add_notes_row_txt_note_row) as TextView
         val imageButton=itemView.findViewById(R.id.add_notes_row_remove_row) as ImageButton
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.add_notes_row,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return noteList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.textView.text=noteList[position]
        with(holder) {
            imageButton.setOnClickListener(View.OnClickListener {
            noteList.removeAt(position)
                setNoteList(noteList)
        })
        }
    }
    internal fun setNoteList(list: MutableList<String>){
        noteList=list
        notifyDataSetChanged()
    }
    internal fun getNoteList()=noteList
    internal fun addNewNote(note:String){
        noteList.add(note)
        notifyDataSetChanged()
    }
}