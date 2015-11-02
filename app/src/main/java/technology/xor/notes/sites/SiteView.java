package technology.xor.notes.sites;

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
import technology.xor.notes.database.CipherSites;
import technology.xor.notes.notes.R;

public class SiteView extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<CipherSites> cSites;
    public static SiteView newInstance() {
        return new SiteView();
    }

    public SiteView() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_site_list, container, false);
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.siteList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ShowCards();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cSites.clear();
        ShowCards();
    }

    private void ShowCards() {

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(getActivity());
        cSites = dbHelper.GetSites();

        if (!cSites.isEmpty()) {
            SiteAdapter mAdapter = new SiteAdapter(cSites, R.layout.fragment_sites, getContext());

            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.addSiteBtn:
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                Intent addNote = new Intent(getActivity(), NewSite.class);
                startActivity(addNote);
                return true;
            case R.id.deleteAllSiteBtn:
                Toast.makeText(getActivity().getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.siteview, menu);
    }
}

