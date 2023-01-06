package ru.synergy.roomdemonotesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ru.synergy.roomdemonotesapp.adapter.NotesAdapter;
import ru.synergy.roomdemonotesapp.notedb.NoteDatabase;
import ru.synergy.roomdemonotesapp.notedb.model.Note;

public class NoteListActivity extends AppCompatActivity implements NotesAdapter.OnNoteItemClick {

    private TextView textViewMsg;
    private RecyclerView recyclerView;
    private NoteDatabase noteDatabase;
    private List<Note> notes;
    private NotesAdapter notesAdapter;
    private int pos;
    private ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() > 0) {
                if (result.getResultCode() == 1) {
                    notes.add((Note) result.getData().getSerializableExtra("note"));
                } else if (result.getResultCode() == 2) {
                    notes.set(pos, (Note) result.getData().getSerializableExtra("note"));
                }
                listVisibility();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
    }

    private void displayList() {
        noteDatabase = NoteDatabase.getInstance(NoteListActivity.this);
        new RetrieveTask(this).execute();
    }

    private static class RetrieveTask extends AsyncTask<Void, Void, List<Note>> {

        private WeakReference<NoteListActivity> activityReference;

        //only retain a weak reference to the activity
        RetrieveTask(NoteListActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Note> doInBackground(Void... voids) {
            if (activityReference.get() != null) {
                return activityReference.get().noteDatabase.getNoteDao().getNotes();
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            if (notes != null && notes.size() > 0) {
                activityReference.get().notes.clear();
                activityReference.get().notes.addAll(notes);
                //hides empty text view
                activityReference.get().textViewMsg.setVisibility(View.GONE);
                activityReference.get().notesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initializeView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(Toolbar);
        textViewMsg = (TextView) findViewById(R.id.tv__empty);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(listener);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(NoteListActivity.this));
        notes = new ArrayList<>();
        notesAdapter = new NotesAdapter(notes, NoteListActivity.this);
        recyclerView.setAdapter(notesAdapter);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            activityResultLauncher.launch(new Intent(NoteListActivity.this, AddNoteActivity.class));

        }
    };


    public void onNoteClick(final int pos) {
        new AlertDialog.Builder(NoteListActivity.this)
                .setTitle("Select Options")
                .setItems(new String[]{"Delete", "Update"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                noteDatabase.getNoteDao().deleteNote(notes.get(pos));
                                notes.remove(pos);
                                listVisibility();
                                break;
                            case 1:
                                NoteListActivity.this.pos = pos;
                                startActivityForResult( new Intent(NoteListActivity.this, AddNoteActivity.class).putExtra("note", notes.get(pos)), 100);
                                break;
                        }
                    }
                }).show();
    }

    private void listVisibility() {
        int emptyMsgVisibility = View.GONE;
        if (notes.size() == 0) { // no item to display
            if (textViewMsg.getVisibility() == View.GONE)
                emptyMsgVisibility = View.VISIBLE;
        }
        textViewMsg.setVisibility(emptyMsgVisibility);
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy(){
        noteDatabase.cleanUp();
        super.onDestroy();
    }
}
