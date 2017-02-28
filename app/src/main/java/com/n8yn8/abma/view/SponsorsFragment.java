package com.n8yn8.abma.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BSponsor;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.view.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SponsorsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SponsorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SponsorsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private List<BSponsor> sponsors = new ArrayList<>();

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

        final GridView gridview = (GridView) rootView.findViewById(R.id.gridView);

        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
        sponsors = db.getAllYears().get(0).getSponsors();
        gridview.setAdapter(new ImageAdapter(getActivity(), sponsors));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String urlString = sponsors.get(position).getUrl();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}

