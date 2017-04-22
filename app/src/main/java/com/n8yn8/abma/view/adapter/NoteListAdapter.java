package com.n8yn8.abma.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

/**
 * Created by Nate on 3/15/15.
 */
public class NoteListAdapter extends ArrayAdapter<BNote> {

    private final Activity context;
    private final List<BNote> notes;
    private DatabaseHandler db;

    static class ViewHolder {
        public TextView noteTextView;
        public TextView detailTextView;
    }

    public NoteListAdapter(Activity context, List<BNote> notes) {
        super(context, R.layout.item_list_notes, notes);
        this.context = context;
        this.notes = notes;
        db = new DatabaseHandler(context);
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
        BNote note = notes.get(position);
        holder.noteTextView.setText(note.getContent());
        String title = getTitle(note);

        holder.detailTextView.setText(title);

        return rowView;
    }

    @Override
    public BNote getItem(int position) {
        return super.getItem(position);
    }

    private String getTitle(BNote note) {
        if (note.getPaperId() != null) {
            BPaper paper = db.getPaperById(note.getPaperId());
            if (paper != null) {
                return paper.getTitle();
            }
        } else {
            BEvent event = db.getEventById(note.getEventId());
            if (event != null) {
                return event.getTitle();
            }
        }
        Utils.logError("Set Note Title", note.toString());
        return "";
    }
}
