package co.devcon.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.devcon.mynotes.adapter.NotesAdapter;
import co.devcon.mynotes.data.Reference;
import co.devcon.mynotes.model.NoteModel;

public class MainActivity extends AppCompatActivity {

    private NotesAdapter mAdapter;

    private final static int NOTE_ADD = 1000;

    private DatabaseReference mNotesReference;

    private ArrayList<String> mKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mKeys = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                startActivityForResult(intent, NOTE_ADD);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotesAdapter(this, new NotesAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                NoteModel model = mAdapter.getItem(pos);
                // Open back note activity with data
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra(Reference.NOTE_ID, mKeys.get(pos));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        mNotesReference = FirebaseDatabase.getInstance().getReference("1234").child(Reference.DB_NOTES);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // listening for changes
        mNotesReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear table
                mKeys.clear();
                mAdapter.clear();
                // load data
                for(DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    NoteModel model = noteSnapshot.getValue(NoteModel.class);
                    mAdapter.addData(model);
                    mKeys.add(noteSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stop listening
                mNotesReference.addValueEventListener(null);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
