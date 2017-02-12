package com.n8yn8.abma;

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

import java.util.Arrays;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SponsorsFragment.
     */
    // TODO: Rename and change types and number of parameters
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

                GridView gridview = (GridView) rootView.findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        final List<String> links = Arrays.asList("http://www.brevardzoo.org/",
        "http://www.centralfloridazoo.org/",
        "http://www.lowryparkzoo.org/",
        "http://www.sfcollege.edu/zoo/",
        "http://www.seewinter.com/",
        "http://www.flaquarium.org/",
        "https://seaworldparks.com/seaworld-orlando?&gclid=CNnZ_rOg5ssCFUQbgQodW_gLyg&dclid=CMvQhLSg5ssCFUQFgQod384IRQ",
        "http://naturalencounters.com/",
        "http://www.precisionbehavior.com/",
        "http://www.animaledu.com/Home/d/1",
        "https://seaworldparks.com/en/buschgardens-tampa/?&gclid=CM7sh5ig5ssCFYclgQodFi4MFg&dclid=CLD5i5ig5ssCFQsNgQodm1IJ_g",
        "http://www.flaza.org/zoos--aquariums.html",
        "http://tampabayaazk.weebly.com/");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String urlString = links.get(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

