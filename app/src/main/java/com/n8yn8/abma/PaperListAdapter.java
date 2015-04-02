package com.n8yn8.abma;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nate on 3/22/15.
 */
public class PaperListAdapter extends ArrayAdapter<Paper> {
    private final Activity context;
    private final List<Paper> papers;

    static class ViewHolder {
        public TextView titleTextView;
        public TextView authorTextView;
    }

    public PaperListAdapter(Activity context, List<Paper> papers) {
        super(context, R.layout.item_list_paper, papers);
        this.context = context;
        this.papers = papers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_paper, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.paperTitleTextView);
            viewHolder.authorTextView = (TextView) rowView.findViewById(R.id.paperAuthorTextView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Paper paper = papers.get(position);
        holder.titleTextView.setText(paper.getTitle());
        holder.authorTextView.setText(paper.getAuthor());

        return rowView;
    }

    @Override
    public Paper getItem(int position) {
        return super.getItem(position);
    }
}
