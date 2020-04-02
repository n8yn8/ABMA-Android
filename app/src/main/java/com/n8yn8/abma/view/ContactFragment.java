package com.n8yn8.abma.view;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

    private List<Survey> surveys = new ArrayList<>();
    private SurveyListAdapter adapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactFragment.
     */
    static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        RecyclerView listView = view.findViewById(R.id.surveyListView);
        adapter = new SurveyListAdapter(new SurveyListAdapter.OnLinkClickedListener() {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext().getApplicationContext());
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
                db.surveyDao().getSurveys(year.objectId).observe(getViewLifecycleOwner(), new Observer<List<Survey>>() {
                    @Override
                    public void onChanged(List<Survey> allSurveys) {
                        Date now = new Date();
                        for (Survey survey : allSurveys) {
                            if (now.after(new Date(survey.startDate)) && now.before(new Date(survey.endDate))) {
                                surveys.add(survey);
                            }
                        }
                        List<SurveyListAdapter.BaseData> objects = new ArrayList<>();
                        objects.add(new SurveyListAdapter.BaseData("Surveys"));
                        if (surveys.isEmpty()) {
                            objects.add(new SurveyListAdapter.BaseData(SurveyListAdapter.NO_SURVEY_TITLE));
                        } else {
                            for (Survey survey : surveys) {
                                objects.add(new SurveyListAdapter.SurveyData(survey));
                            }
                        }
                        objects.add(new SurveyListAdapter.BaseData("Links"));
                        objects.add(new SurveyListAdapter.Link("https://www.theabma.org", "ABMA Website"));
                        objects.add(new SurveyListAdapter.Link("https://theabma.org/contact/", "Contact ABMA"));
                        adapter.submitList(objects);
                    }
                });

            }
        });
    }
}
