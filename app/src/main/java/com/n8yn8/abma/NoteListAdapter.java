package com.n8yn8.abma;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nate on 3/15/15.
 */
public class NoteListAdapter  extends ArrayAdapter<Note> {

    private final Activity context;
    private final List<Note> notes;

    static class ViewHolder {
        public TextView noteTextView;
        public TextView detailTextView;
    }

    public NoteListAdapter(Activity context, List<Note> notes) {
        super(context, R.layout.fragment_note_list, notes);
        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_notes, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.noteTextView = (TextView) rowView.findViewById(R.id.noteTitleTextView);
            viewHolder.detailTextView = (TextView) rowView.findViewById(R.id.noteDetailTextView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.noteTextView.setText(notes.get(position).getContent());
        //TODO: find event
        holder.detailTextView.setText("Index = " + notes.get(position).getEventId());

        return rowView;
    }

    @Override
    public Note getItem(int position) {
        return super.getItem(position);
    }
}
