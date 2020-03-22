package com.n8yn8.abma.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.view.adapter.ImageAdapter;

import java.util.List;


public class SponsorsFragment extends Fragment {

    private ImageAdapter imageAdapter;

    public SponsorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SponsorsFragment.
     */
    static SponsorsFragment newInstance() {
        return new SponsorsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sponsors, container, false);

        final RecyclerView recyclerView = rootView.findViewById(R.id.sponsors_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        imageAdapter = new ImageAdapter(requireActivity(), new ImageAdapter.OnClickListener() {
            @Override
            public void onClick(Sponsor sponsor) {
                if (!TextUtils.isEmpty(sponsor.url)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sponsor.url)));
                }
            }
        });
        recyclerView.setAdapter(imageAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SponsorViewModel viewModel = new ViewModelProvider(this).get(SponsorViewModel.class);
        viewModel.getSponsors().observe(getViewLifecycleOwner(), new Observer<List<Sponsor>>() {
            @Override
            public void onChanged(List<Sponsor> sponsors) {
                imageAdapter.submitList(sponsors);
            }
        });
    }
}

