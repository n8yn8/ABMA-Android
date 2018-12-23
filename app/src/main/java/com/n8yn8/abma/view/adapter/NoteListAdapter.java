package com.n8yn8.abma.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Paper;

import java.util.List;

/**
 * Created by Nate on 3/15/15.
 */
public class NoteListAdapter extends ArrayAdapter<Note> {

    private final Activity context;
    private final List<Note> notes;
    private AppDatabase db;

    static class ViewHolder {
        TextView noteTextView;
        public TextView detailTextView;
    }

    public NoteListAdapter(Activity context, List<Note> notes) {
        super(context, R.layout.item_list_notes, notes);
        this.context = context;
        this.notes = notes;
        db = AppDatabase.getInstance(context.getApplicationContext());
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_notes, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.noteTextView = rowView.findViewById(R.id.noteTitleTextView);
            viewHolder.detailTextView = rowView.findViewById(R.id.noteDetailTextView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Note note = notes.get(position);
        holder.noteTextView.setText(note.content);
        String title = getTitle(note);

        holder.detailTextView.setText(title);

        return rowView;
    }

    @Override
    public Note getItem(int position) {
        return super.getItem(position);
    }

    private String getTitle(Note note) {
        if (note.paperId != null) {
            Paper paper = db.paperDao().getPaperById(note.paperId);
            if (paper != null) {
                return paper.title;
            }
        } else {
            Event event = db.eventDao().getEventById(note.eventId);
            if (event != null) {
                return event.title;
            }
        }
        Utils.logError("Set Note Title", note.toString());
        return "";
    }
}
