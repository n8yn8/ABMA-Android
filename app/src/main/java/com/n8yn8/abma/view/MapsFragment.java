package com.n8yn8.abma.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n8yn8.abma.App;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.entities.Map;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.view.adapter.MapsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private List<Map> maps = new ArrayList<>();

    public MapsFragment() {

    }

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        RecyclerView listView = view.findViewById(R.id.mapsListView);
        MapsAdapter adapter = new MapsAdapter(((App) getContext().getApplicationContext()).getImageLoader(), maps, new MapsAdapter.OnMapClickListener() {
            @Override
            public void onClick(Map map) {
                MapDetailActivity.start(getContext(), map);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getYear().observe(getViewLifecycleOwner(), new Observer<Year>() {
            @Override
            public void onChanged(Year year) {
                AppDatabase db = AppDatabase.getInstance(requireActivity().getApplicationContext());
                maps.addAll( db.mapDao().getMaps(year.objectId));
            }
        });
    }
}
