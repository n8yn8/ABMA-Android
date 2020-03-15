package com.n8yn8.abma.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.view.NoteModel;

import java.util.List;

/**
 * Created by Nate on 3/15/15.
 */
public class NoteListAdapter extends ArrayAdapter<NoteModel> {

    private final Activity context;
    private final List<NoteModel> notes;

    static class ViewHolder {
        TextView noteTextView;
        public TextView detailTextView;
    }

    public NoteListAdapter(Activity context, List<NoteModel> notes) {
        super(context, R.layout.item_list_notes, notes);
        this.context = context;
        this.notes = notes;
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
        NoteModel noteModel = notes.get(position);
        holder.noteTextView.setText(noteModel.getNote().content);
        String title = getTitle(noteModel);

        holder.detailTextView.setText(title);

        return rowView;
    }

    @Override
    public NoteModel getItem(int position) {
        return super.getItem(position);
    }

    private String getTitle(NoteModel noteModel) {
        if (noteModel.getPaper() != null) {
            Paper paper = noteModel.getPaper();
            if (paper != null) {
                return paper.title;
            }
        } else {
            Event event = noteModel.getEvent();
            if (event != null) {
                return event.title;
            }
        }
        Utils.logError("Set Note Title", noteModel.getNote().toString());
        return "";
    }
}
