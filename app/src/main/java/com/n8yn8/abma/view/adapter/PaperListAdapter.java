package com.n8yn8.abma.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Paper;

import java.util.List;

/**
 * Created by Nate on 3/22/15.
 */
public class PaperListAdapter extends ArrayAdapter<Paper> {
    private final Activity context;
    private final List<Paper> papers;

    static class ViewHolder {
        public TextView titleTextView;
        TextView authorTextView;
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
            viewHolder.titleTextView = rowView.findViewById(R.id.paperTitleTextView);
            viewHolder.authorTextView = rowView.findViewById(R.id.paperAuthorTextView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Paper paper = papers.get(position);
        holder.titleTextView.setText(paper.title);
        holder.authorTextView.setText(paper.author);

        return rowView;
    }

    @Override
    public Paper getItem(int position) {
        return super.getItem(position);
    }
}
