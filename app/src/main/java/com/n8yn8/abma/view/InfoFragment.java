package com.n8yn8.abma.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.AppDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        TextView infoTextView = view.findViewById(R.id.infoTextView);


        String info = AppDatabase.getInstance(getActivity().getApplicationContext()).yearDao().getLastYear().info;
        infoTextView.setText(info);
        return view;
    }


}
