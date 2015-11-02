package technology.xor.notes.sites;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import technology.xor.notes.database.CipherSites;
import technology.xor.notes.notes.R;
import technology.xor.notes.support.CameraView;


public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder> {

    private ArrayList<CipherSites> sites;
    private int rowLayout;
    private Context mContext;

    public SiteAdapter(ArrayList<CipherSites> sites, int rowLayout, Context context) {
        this.sites = sites;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final CipherSites site = sites.get(i);
        viewHolder.siteId.setText(site.GetSiteId());
        viewHolder.date.setText(site.GetDateCreated());
        viewHolder.location.setText(site.GetSiteLocation());

        viewHolder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, CameraView.class);
                mIntent.putExtra("site_id", site.GetSiteId());
                mContext.startActivity(mIntent);
            }
        });

        viewHolder.noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, SiteNoteView.class);
                mIntent.putExtra("site_id", site.GetSiteId());
                mContext.startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sites == null ? 0 : sites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView location;
        private TextView siteId;

        private ImageButton noteBtn;
        private ImageButton imgBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            siteId = (TextView) itemView.findViewById(R.id.siteName);
            date = (TextView) itemView.findViewById(R.id.dateCreated);
            location = (TextView) itemView.findViewById(R.id.siteLocation);

            noteBtn = (ImageButton) itemView.findViewById(R.id.addNoteBtn2);
            imgBtn = (ImageButton) itemView.findViewById(R.id.addImgBtn);
        }

    }
}
