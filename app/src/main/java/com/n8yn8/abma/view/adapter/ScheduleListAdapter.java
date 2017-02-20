package com.n8yn8.abma.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.old.Event;

import java.util.ArrayList;

/**
 * Created by Nate on 2/15/15.
 */
public class ScheduleListAdapter extends ArrayAdapter<Event> {

    private final Activity context;
    private final ArrayList<Event> events;

    static class ViewHolder {
        public TextView eventTitleTextView;
        public TextView timeTextView;
    }

    public ScheduleListAdapter(Activity context, ArrayList<Event> events) {
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
        holder.eventTitleTextView.setText(events.get(position).getTitle());
        holder.timeTextView.setText(events.get(position).getTime());

        return rowView;
    }

    @Override
    public Event getItem(int position) {
        return super.getItem(position);
    }
}
