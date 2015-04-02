package com.n8yn8.abma;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
        TextView infoTextView = (TextView) view.findViewById(R.id.infoTextView);
        infoTextView.setText("Badges\n" +
                "Please make sure to wear your badges throughout the conference. These badges are your admission to the various events and programming.\n" +
                "        \n" +
                "Behavioral Management Fund\n" +
                "The BMF Committee overlooks the Scholarship/Grant process by developing the criteria for the application, review of the applications, and selection of a recipient. The committee is also responsible for the fundraising and development of the Behavior Management Fund. The proceeds a portion of the silent auction support the Behavioral Management Fund.\n" +
                "\n" +
                "ABMA Travel Scholarship\n" +
                "The purpose of this scholarship is to assist an ABMA member who would otherwise be unable to secure financial support to attend the conference. The Travel Scholarship will help the award recipient by giving them the ability to present their work and it will help the organization by giving ABMA members the opportunity to hear presentations that they would not have otherwise been able to. The Travel Scholarship supports the ABMA Core Value of “Sharing the Knowledge”. The Travel Scholarship is made possible by the Behavior Management Fund (BMF) Committee.\n" +
                "This year’s recipient is Anaka Nazareth from the Maymont Foundation in Richmond, Virginia. Her paper is titled \"A chicken's choice: positive reinforcement training vs free food\". The presentation will share the results of her Master's thesis research project in Animal Behavior and Conservation.\n" +
                "For more Travel Scholarship information or to apply for next year’s conference, please visit www.theabma.org.\n" +
                "\n" +
                "Conservation Gift\n" +
                "Each year the BMF provides a conservation-related gift to conference delegates.\n" +
                "\n" +
                "Business Services\n" +
                "Business services can be found at the reception desk. There is free Wi-Fi throughout the hotel.\n" +
                "\n" +
                "Conference Survey\n" +
                "Once again this year ABMA is being green and doing the conference surveys online. The results of these surveys help the ABMA to make each conference successful and better suited to the members’ needs. Your responses are greatly valued and do ensure the ABMA’s future conference programming is suited to the interests of our members. The survey will be emailed to registered delegates at the closing of the conference or accessed by the following link:\n" +
                "http://survey.constantcontact.com/survey/a07eaicm35mi5pus5o9/start\n" +
                "These surveys are an important component to the assessment of the ABMA, and we thank you for your time in completing them. If you do not receive a conference survey, please contact the Research and Evaluation Committee Chair, Darren Minier, at deminier@gmail.com.\n" +
                "\n" +
                "Silent Auction Donations\n" +
                "Silent auction items can be dropped off at registration. If you did not already, please send us an email with the item you are bringing and its value, please let us know when you drop the item off so that we can create a bid sheet.\n" +
                "\n" +
                "Transportation\n" +
                "Bicycles can be borrowed from the hotel\n" +
                "The train and bus station is a 10-minute walk from the hotel\n" +
                "The Nyborg taxi can be ordered at the hotel reception or at the train/bus station");
        return view;
    }


}
