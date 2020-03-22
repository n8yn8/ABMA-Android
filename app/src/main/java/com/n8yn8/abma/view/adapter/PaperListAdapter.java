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
import com.n8yn8.abma.model.entities.Paper;

/**
 * Created by Nate on 3/22/15.
 */
public class PaperListAdapter extends ListAdapter<Paper, PaperListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Paper> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Paper>() {
                @Override
                public boolean areItemsTheSame(@NonNull Paper oldItem, @NonNull Paper newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull Paper oldItem, @NonNull Paper newItem) {
                    return oldItem.equals(newItem);
                }
            };
    private OnClickListener onClickListener;

    public PaperListAdapter(OnClickListener onClickListener) {
        super(DIFF_CALLBACK);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_paper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), onClickListener);
    }

    public interface OnClickListener {
        void onClick(Paper paper);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.paperTitleTextView);
            authorTextView = itemView.findViewById(R.id.paperAuthorTextView);
        }

        void bind(final Paper paper, final OnClickListener onClickListener) {
            titleTextView.setText(paper.title);
            authorTextView.setText(paper.author);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(paper);
                }
            });
        }
    }
}
