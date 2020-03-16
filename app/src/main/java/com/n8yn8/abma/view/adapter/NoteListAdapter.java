package com.n8yn8.abma.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.view.NoteModel;

/**
 * Created by Nate on 3/15/15.
 */
public class NoteListAdapter extends ListAdapter<NoteModel, NoteListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<NoteModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NoteModel>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull NoteModel oldNoteModel, @NonNull NoteModel newNoteModel) {
                    return oldNoteModel.getNote().id.equals(newNoteModel.getNote().id);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull NoteModel oldNoteModel, @NonNull NoteModel newNoteModel) {
                    return oldNoteModel.getNote().equals(newNoteModel.getNote());
                }
            };
    private OnClickListener onClickListener;

    public NoteListAdapter(OnClickListener onClickListener) {
        super(DIFF_CALLBACK);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_notes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final NoteModel noteModel = getItem(position);
        holder.bind(noteModel, onClickListener);
    }

    public interface OnClickListener {
        void onClick(NoteModel noteModel);

        void onLongClick(NoteModel noteModel);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTextView;
        TextView detailTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.noteTitleTextView);
            detailTextView = itemView.findViewById(R.id.noteDetailTextView);
        }

        private static String getTitle(NoteModel noteModel) {
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

        void bind(final NoteModel noteModel, final OnClickListener onClickListener) {
            noteTextView.setText(noteModel.getNote().content);
            detailTextView.setText(getTitle(noteModel));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(noteModel);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onClickListener.onLongClick(noteModel);
                    return true;
                }
            });
        }
    }
}
