package com.n8yn8.abma.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BSurvey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nate on 3/26/18.
 */

public class SurveyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = SurveyListAdapter.class.getSimpleName();

    private enum ViewType {
        SURVEY, LINK, SEPARATOR;

    }

    private List<Object> objects = new ArrayList<>();

    public SurveyListAdapter(List<BSurvey> surveys) {
        objects.add("Surveys");
        if (surveys.isEmpty()) {
            objects.add("No Surveys Available");
        } else {
            objects.addAll(surveys);
        }
        objects.add("Links");
        objects.add(new Link("https://www.theabma.org", "ABMA Website"));
        objects.add(new Link("https://theabma.org/contact/", "Contact ABMA"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (ViewType.values()[viewType]) {
            case SURVEY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_survey, parent, false);
                return new SurveyViewHolder(view);
            case LINK:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_link, parent, false);
                return new LinkViewHolder(view);
            case SEPARATOR:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_header, parent, false);
                return new SeparatorViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Object item = objects.get(position);

        switch (ViewType.values()[holder.getItemViewType()]) {
            case SURVEY:
                if (item instanceof BSurvey && holder instanceof SurveyViewHolder) {
                    ((SurveyViewHolder) holder).configureSurvey((BSurvey) item);
                } else {
                    Log.e(TAG, "Not survey");
                }
            case LINK:
                if (item instanceof Link && holder instanceof LinkViewHolder) {
                    ((LinkViewHolder) holder).configureLink((Link) item);
                } else {
                    Log.e(TAG, "Not link");
                }
            case SEPARATOR:
                if (item instanceof String && holder instanceof SeparatorViewHolder) {
                    ((SeparatorViewHolder) holder).configureTitle((String) item);
                } else {
                    Log.e(TAG, "Not string");
                }

        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = objects.get(position);
        if (object instanceof BSurvey) {
            return ViewType.SURVEY.ordinal();
        } else if (object instanceof Link){
            return ViewType.LINK.ordinal();
        } else {
            return ViewType.SEPARATOR.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private class Link {

        private String url;
        private String title;

        Link(String url, String title) {
            this.url = url;
            this.title = title;
        }
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView detailsTextView;
        TextView timeTextView;

        SurveyViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.surveyTitleTextView);
            detailsTextView = (TextView) itemView.findViewById(R.id.surveyDescTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.surveyTimeTextView);
        }

        void configureSurvey(BSurvey survey) {
            nameTextView.setText(survey.getTitle());
            detailsTextView.setText(survey.getDetails());
            timeTextView.setText(String.format(timeTextView.getContext().getString(R.string.available_until), survey.getEnd()));
        }
    }

    public class LinkViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        LinkViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.linkTextView);
        }

        void configureLink(Link link) {
            titleTextView.setText(link.title);
        }
    }

    public class SeparatorViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        SeparatorViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.separatorTitleView);
        }

        void configureTitle(String title) {
            titleTextView.setText(title);
        }
    }
}
