package technology.xor.notes.sites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherNotes;
import technology.xor.notes.notes.NewNote;
import technology.xor.notes.notes.NoteAdapter;
import technology.xor.notes.notes.R;

public class SiteNoteView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private ArrayList<CipherNotes> cNotes;
    private String siteId;

    public SiteNoteView() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_site_note_list);

        TextView tv = (TextView) findViewById(R.id.siteId);
        mRecyclerView = (RecyclerView) findViewById(R.id.siteNoteList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent mIntent = getIntent();
        siteId = mIntent.getStringExtra("site_id");
        tv.setText(siteId + " - Notes");

        ShowCards();
    }

    @Override
    public void onResume() {
        super.onResume();
        cNotes.clear();
        ShowCards();
    }

    private void ShowCards() {

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(this);
        cNotes = dbHelper.GetSiteNotes(siteId);

        if (!cNotes.isEmpty()) {
            mAdapter = new NoteAdapter(cNotes, R.layout.fragment_notes, this);
        }

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: CHANGE MENU BUTTONS

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.addNoteBtn:

                Intent addNote = new Intent(this, NewNote.class);
                addNote.putExtra("isSite", true);
                addNote.putExtra("siteName", siteId);
                startActivity(addNote);

                return true;
            case R.id.deleteAllNoteBtn:
                Toast.makeText(this.getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.noteview, menu);
        return true;
    }
}

