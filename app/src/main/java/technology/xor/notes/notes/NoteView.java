package technology.xor.notes.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherNotes;

public class NoteView extends Fragment {

    private static final String TAG = "NoteView";
    private static final String NOTE_ONLY = "note";
    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private ArrayList<CipherNotes> cNotes;

    public static NoteView newInstance() {
        return new NoteView();
    }

    public NoteView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.noteList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ShowCards();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cNotes.clear();
        ShowCards();
    }

    private void ShowCards() {

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(getActivity());
        cNotes = dbHelper.GetNotes();

        if (!cNotes.isEmpty()) {
            mAdapter = new NoteAdapter(cNotes, R.layout.fragment_notes, getContext());
        }

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.addNoteBtn:

                Intent addNote = new Intent(getActivity(), NewNote.class);
                addNote.putExtra("isSite", false);
                addNote.putExtra("siteName", NOTE_ONLY);
                startActivity(addNote);

                return true;
            case R.id.deleteAllNoteBtn:
                Toast.makeText(getActivity().getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.noteview, menu);
    }
}
