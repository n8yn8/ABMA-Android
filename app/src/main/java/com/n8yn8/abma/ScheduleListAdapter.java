package com.n8yn8.abma;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Nate on 2/15/15.
 */
public class ScheduleListAdapter extends ArrayAdapter<Map<String, String>> {

    private final Activity context;
    private final ArrayList<Map<String, String>> events;

    static class ViewHolder {
        public TextView eventTitleTextView;
        public TextView timeTextView;
    }

    public ScheduleListAdapter(Activity context, ArrayList<Map<String, String>> events) {
        super(context, R.layout.item_list_schedule, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_schedule, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.eventTitleTextView = (TextView) rowView.findViewById(R.id.eventTitleTextView);
            viewHolder.timeTextView = (TextView) rowView.findViewById(R.id.eventTimeTextView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.eventTitleTextView.setText(events.get(position).get("Title"));
        holder.timeTextView.setText(events.get(position).get("Time"));

        return rowView;
    }

    @Override
    public Map<String, String> getItem(int position) {
        return super.getItem(position);
    }
}
