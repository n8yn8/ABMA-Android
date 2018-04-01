package com.n8yn8.abma.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BMap;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.view.adapter.MapsAdapter;

import java.util.List;

public class MapsFragment extends Fragment {

    private List<BMap> maps;

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    public MapsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler db = new DatabaseHandler(getContext());
        BYear latestYear = db.getLastYear();
        maps = db.getMaps(latestYear.getObjectId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.mapsListView);
        MapsAdapter adapter = new MapsAdapter(getContext(), maps, new MapsAdapter.OnMapClickListener() {
            @Override
            public void onClick(BMap map) {

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        listView.setAdapter(adapter);

        return view;
    }
}
