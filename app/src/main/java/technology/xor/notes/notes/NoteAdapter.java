package technology.xor.notes.notes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import technology.xor.notes.database.CipherNotes;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>  {

    private ArrayList<CipherNotes> notes;
    private int rowLayout;
    private Context mContext;

    public NoteAdapter(ArrayList<CipherNotes> notes, int rowLayout, Context context) {
        this.notes = notes;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        CipherNotes note = notes.get(i);
        viewHolder.date.setText(note.GetDate());
        viewHolder.location.setText(note.GetLocation());
        viewHolder.bNote.setText(note.GetNote());

        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView location;
        private TextView bNote;
        private ImageButton editBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            bNote = (TextView) itemView.findViewById(R.id.basicNote);
            date = (TextView) itemView.findViewById(R.id.noteDate);
            location = (TextView) itemView.findViewById(R.id.noteLocation);
            editBtn = (ImageButton) itemView.findViewById(R.id.noteEditBtn);
        }

    }
}
