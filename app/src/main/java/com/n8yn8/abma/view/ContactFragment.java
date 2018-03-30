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
import com.n8yn8.abma.model.backendless.BSurvey;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.view.adapter.SurveyListAdapter;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    private RecyclerView listView;
    List<BSurvey> surveys;

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

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler db = new DatabaseHandler(getContext());
        BYear latestYear = db.getLastYear();
        surveys = db.getSurveys(latestYear.getObjectId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = (RecyclerView) view.findViewById(R.id.surveyListView);
        SurveyListAdapter adapter = new SurveyListAdapter(surveys);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        listView.setAdapter(adapter);

//        Button siteButton = (Button) view.findViewById(R.id.siteButton);
//        Button contactButton = (Button) view.findViewById(R.id.contactButton);
//
//        siteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.theabma.org"));
//                startActivity(browserIntent);
//            }
//        });
//        contactButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://theabma.org/contact/"));
//                startActivity(browserIntent);
//            }
//        });
        return view;
    }


}
