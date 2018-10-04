package com.n8yn8.abma.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.ConvertUtil;
import com.n8yn8.abma.model.backendless.BSponsor;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.view.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;



public class SponsorsFragment extends Fragment {
    private List<Sponsor> sponsors = new ArrayList<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SponsorsFragment.
     */
    public static SponsorsFragment newInstance() {
        SponsorsFragment fragment = new SponsorsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SponsorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sponsors, container, false);

        final GridView gridview = rootView.findViewById(R.id.gridView);
        final ImageAdapter imageAdapter = new ImageAdapter(getActivity(), sponsors);

        final AppDatabase db = AppDatabase.getInstance(getActivity().getApplicationContext());
        final Year year = db.yearDao().getLastYear();
        sponsors.clear();
        sponsors.addAll(db.sponsorDao().getSponsors(year.objectId));
        if (sponsors.size() == 0) {
            DbManager.getInstance().getSponsors(year.objectId, new DbManager.Callback<List<BSponsor>>() {
                @Override
                public void onDone(List<BSponsor> bSponsors, String error) {
                    Log.d("Nate", "onDone");
                    List<Sponsor> retrievedSponsors = new ArrayList<>(ConvertUtil.convertSponsors(bSponsors, year.objectId));
                    db.sponsorDao().insert(retrievedSponsors);
                    sponsors.clear();
                    sponsors.addAll(retrievedSponsors);
                    imageAdapter.notifyDataSetChanged();
                }
            });
        }
        Log.d("Nate", "setAdapter");
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String urlString = sponsors.get(position).url;
                if (!TextUtils.isEmpty(urlString)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
                }
            }
        });

        return rootView;
    }

}

