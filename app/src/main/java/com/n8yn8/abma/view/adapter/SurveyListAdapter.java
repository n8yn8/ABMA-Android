package com.n8yn8.abma.view.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Survey;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Nate on 3/26/18.
 */

public class SurveyListAdapter extends ListAdapter<SurveyListAdapter.BaseData, RecyclerView.ViewHolder> {

    private static final DiffUtil.ItemCallback<BaseData> DIFF_CALLBACK = new DiffUtil.ItemCallback<BaseData>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull BaseData oldItem, @NonNull BaseData newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull BaseData oldItem, @NonNull BaseData newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final static String TAG = SurveyListAdapter.class.getSimpleName();
    public final static String NO_SURVEY_TITLE = "No Surveys Available";
    private OnLinkClickedListener onLinkClickedListener;

    public SurveyListAdapter(OnLinkClickedListener onLinkClickedListener) {
        super(DIFF_CALLBACK);
        this.onLinkClickedListener = onLinkClickedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (ViewType.values()[viewType]) {
            case SURVEY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_survey, parent, false);
                return new SurveyViewHolder(view);
            case NO_SURVEY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_link, parent, false);
                return new NoSurveyViewHolder(view);
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

        BaseData item = getItem(position);

        switch (ViewType.values()[holder.getItemViewType()]) {
            case SURVEY:
                if (item instanceof SurveyData && holder instanceof SurveyViewHolder) {
                    ((SurveyViewHolder) holder).onBind((SurveyData) item, onLinkClickedListener);
                } else {
                    Log.e(TAG, "Not survey");
                }
                break;
            case NO_SURVEY:
                if (holder instanceof NoSurveyViewHolder) {
                    ((NoSurveyViewHolder) holder).onBind(item, onLinkClickedListener);
                } else {
                    Log.e(TAG, "Not No Survey Available");
                }
                break;
            case LINK:
                if (item instanceof Link && holder instanceof LinkViewHolder) {
                    ((LinkViewHolder) holder).onBind((Link) item, onLinkClickedListener);
                } else {
                    Log.e(TAG, "Not link");
                }
                break;
            case SEPARATOR:
                if (holder instanceof SeparatorViewHolder) {
                    ((SeparatorViewHolder) holder).onBind(item);
                } else {
                    Log.e(TAG, "Not string");
                }
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseData object = getItem(position);
        if (object instanceof SurveyData) {
            return ViewType.SURVEY.ordinal();
        } else if (object instanceof Link) {
            return ViewType.LINK.ordinal();
        } else if (TextUtils.equals(NO_SURVEY_TITLE, object.title)) {
            return ViewType.NO_SURVEY.ordinal();
        }
        return ViewType.SEPARATOR.ordinal();

    }

    private enum ViewType {
        NO_SURVEY, SURVEY, LINK, SEPARATOR

    }

    public interface OnLinkClickedListener {
        void onClick(String url);
    }

    public static class BaseData {
        String title;

        public BaseData(String title) {
            this.title = title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BaseData baseData = (BaseData) o;
            return title.equals(baseData.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title);
        }
    }

    public static class Link extends BaseData {

        private String url;

        public Link(String url, String title) {
            super(title);
            this.url = url;
        }
    }

    public static class SurveyData extends BaseData {
        private String details;
        private Long endTime;
        private String url;

        public SurveyData(Survey survey) {
            super(survey.title);
            this.details = survey.details;
            this.endTime = survey.endDate;
            this.url = survey.url;
        }
    }

    private static class CommonViewHolder<IViewType> extends RecyclerView.ViewHolder {

        CommonViewHolder(View itemView) {
            super(itemView);
        }

        void onBind(final IViewType object, final OnLinkClickedListener onLinkClickedListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (object instanceof SurveyData) {
                        onLinkClickedListener.onClick(((SurveyData) object).url);
                    } else if (object instanceof Link) {
                        onLinkClickedListener.onClick(((Link) object).url);
                    } else {
                        Log.e(TAG, "not url instance");
                    }
                }
            });
        }
    }

    public static class SurveyViewHolder extends CommonViewHolder<SurveyData> {

        TextView nameTextView;
        TextView detailsTextView;
        TextView timeTextView;

        SurveyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.surveyTitleTextView);
            detailsTextView = itemView.findViewById(R.id.surveyDescTextView);
            timeTextView = itemView.findViewById(R.id.surveyTimeTextView);
        }

        @Override
        void onBind(SurveyData survey, OnLinkClickedListener onLinkClickedListener) {
            super.onBind(survey, onLinkClickedListener);
            nameTextView.setText(survey.title);
            detailsTextView.setText(survey.details);
            timeTextView.setText(String.format(timeTextView.getContext().getString(R.string.available_until), new Date(survey.endTime)));
        }
    }

    public static class LinkViewHolder extends CommonViewHolder<Link> {

        TextView titleTextView;

        LinkViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.linkTextView);
        }

        @Override
        void onBind(Link link, OnLinkClickedListener onLinkClickedListener) {
            super.onBind(link, onLinkClickedListener);
            titleTextView.setText(link.title);
        }
    }

    public static class NoSurveyViewHolder extends CommonViewHolder<BaseData> {

        TextView titleTextView;

        NoSurveyViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.linkTextView);
        }

        @Override
        void onBind(BaseData object, OnLinkClickedListener onLinkClickedListener) {
            super.onBind(object, onLinkClickedListener);
            titleTextView.setText(object.title);
        }
    }

    public static class SeparatorViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        SeparatorViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.separatorTitleView);
        }

        void onBind(BaseData item) {
            titleTextView.setText(item.title);
        }
    }
}
