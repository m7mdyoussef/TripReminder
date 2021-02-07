package com.example.tripreminder2021.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.dataValidation.DataValidator;
import com.example.tripreminder2021.pojo.TripModel;

import java.util.List;
import java.util.zip.Inflater;

public class NoteRecycler extends RecyclerView.Adapter<NoteRecycler.ViewHolder> {

    private List<String> noteList;

    public NoteRecycler(List<String> list)
    {
        this.noteList=list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_notes_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(noteList.get(position));
        holder.imageButton.setOnClickListener(v -> {
            noteList.remove(noteList.get(position));
            setNoteList(noteList);
        });

    }
    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            textView=itemView.findViewById(R.id.add_notes_row_txt_note_row);
            imageButton=itemView.findViewById(R.id.add_notes_row_remove_row);
        }
    }


    private void setNoteList(List<String> list)
    {
        noteList=list;
        notifyDataSetChanged();
    }
    public List<String> getNoteList()
    {
        return noteList;
    }
    public void addNote(String note)
    {
        noteList.add(note);
        notifyDataSetChanged();
    }
}
