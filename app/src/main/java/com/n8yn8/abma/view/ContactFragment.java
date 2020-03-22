package com.n8yn8.abma.view;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.entities.Survey;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.view.adapter.SurveyListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    List<Survey> surveys = new ArrayList<>();

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactFragment.
     */
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getInstance(getActivity().getApplicationContext());
        Year latestYear = db.yearDao().getLastYear();
        List<Survey> allSurveys = db.surveyDao().getSurveys(latestYear.objectId);
        Date now = new Date();
        for (Survey survey : allSurveys) {
            if (now.after(new Date(survey.startDate)) && now.before(new Date(survey.endDate))) {
                surveys.add(survey);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        RecyclerView listView = view.findViewById(R.id.surveyListView);
        SurveyListAdapter adapter = new SurveyListAdapter(surveys, new SurveyListAdapter.OnLinkClickedListener() {
            @Override
            public void onClick(String url) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "URL is not correct", Toast.LENGTH_SHORT).show();
                }

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        listView.setAdapter(adapter);

        return view;
    }


}
