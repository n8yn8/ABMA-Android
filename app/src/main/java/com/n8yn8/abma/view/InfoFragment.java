package com.n8yn8.abma.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Year;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private TextView infoTextView;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        infoTextView = view.findViewById(R.id.infoTextView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getYear().observe(getViewLifecycleOwner(), new Observer<Year>() {
            @Override
            public void onChanged(Year year) {
                if (year != null) {
                    String info = year.info;
                    infoTextView.setText(info);
                } else {
                    infoTextView.setEnabled(false);
                }
            }
        });
    }
}
