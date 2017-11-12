package co.devcon.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.devcon.mynotes.data.Reference;
import co.devcon.mynotes.model.NoteModel;

public class NoteActivity extends AppCompatActivity {

    private EditText mTVTitle;
    private EditText mTVDescription;

    private DatabaseReference mReference;

    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Binding
        mTVTitle = (EditText) findViewById(R.id.et_title);
        mTVDescription = (EditText) findViewById(R.id.et_description);

        mReference = FirebaseDatabase.getInstance().getReference("1234").child(Reference.DB_NOTES);

        Intent intent = getIntent();
        // Load record
        if(intent != null) {
            mId = intent.getStringExtra(Reference.NOTE_ID);
            if(mId != null) {
                mReference.child(mId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NoteModel model = dataSnapshot.getValue(NoteModel.class);
                        if(model != null) {
                            mTVTitle.setText(model.getTitle());
                            mTVDescription.setText(model.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_delete);

        if(mId == null) {
            item.setEnabled(false);
            item.setVisible(false);
        } else {
            item.setEnabled(true);
            item.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:

                // What to do when save
                NoteModel model = new NoteModel(
                        mTVTitle.getText().toString(),
                        mTVDescription.getText().toString(),
                        System.currentTimeMillis()
                );

                save(Reference.DB_NOTES, model, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        actionNotification(databaseError, R.string.done_saved);
                    }
                });
                break;
            case R.id.action_delete:
                if(!mId.isEmpty()) {
                    mReference.child(mId).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            actionNotification(databaseError, R.string.done_deleted);
                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Save record to firebase
     * @param database
     * @param model
     */
    private void save(String database, NoteModel model,
                      DatabaseReference.CompletionListener listener) {

        if(mId == null) {
            // generate id
            mId = mReference.push().getKey();
        }

        mReference.child(mId).setValue(model, listener);
    }

    private void actionNotification(DatabaseError error, int successResourceId) {
        // close activity
        if(error == null) {
            Toast.makeText(NoteActivity.this, successResourceId, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(NoteActivity.this, error.getCode(), Toast.LENGTH_SHORT).show();
        }
    }
}
