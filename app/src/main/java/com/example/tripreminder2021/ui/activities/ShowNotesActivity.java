package com.example.tripreminder2021.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.adapters.NoteRecycler;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;

import java.util.ArrayList;
import java.util.List;

public class ShowNotesActivity extends AppCompatActivity {

    private EditText text;
    private ImageButton imageButton;
    private RecyclerView recyclerView;
    private NoteRecycler notesRecyclerAdapter;
    private List<String> notes=new ArrayList<>();
    private FirebaseDatabaseServices firebaseDatabaseServices;
    private Button btn_save;
    private TripModel trip =new TripModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("ADD_BUNDLE");

        if(args != null)
        {
            trip = (TripModel) args.getSerializable("CURRENT_NOTES");
            for (int i=0;i<trip.getNotes().size();i++)
                if(!trip.getNotes().get(i).isEmpty())
                    notes.add(trip.getNotes().get(i));
            Log.i("TAG", "onCreatehhhh: "+notes.size());
            //notesRecyclerAdapter.setNoteList$Trip_Reminder_app(notes);
        }

        text=findViewById(R.id.txt_view_show_notes);
        btn_save=findViewById(R.id.btn_save_your_notes);

        btn_save.setOnClickListener(it ->{
            firebaseDatabaseServices.updateNotes(trip.getTrip_id(),notes);
            startActivity(new Intent(this,UpcomingTripsActivity.class));
        });

        imageButton=findViewById(R.id.img_btn_add_row);
        recyclerView=findViewById(R.id.add_notes_recycler_view);

        firebaseDatabaseServices=new FirebaseDatabaseServices();

        notesRecyclerAdapter=new NoteRecycler(notes);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(notesRecyclerAdapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNote=text.getText().toString().trim();
                if (newNote ==null || newNote.isEmpty())
                    Toast.makeText(ShowNotesActivity.this, "Enter Valid Note", Toast.LENGTH_SHORT).show();
                else
                    notesRecyclerAdapter.addNote(newNote);
                    text.setText("");
            }
        });
    }
}