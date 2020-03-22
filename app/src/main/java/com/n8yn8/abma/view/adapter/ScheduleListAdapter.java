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

/**
 * Created by Nate on 2/15/15.
 */
public class ScheduleListAdapter extends ListAdapter<Event, ScheduleListAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(Event event);
    }

    private static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Event>() {
                @Override
                public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    return oldItem.equals(newItem);
                }
            };

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView timeTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            timeTextView = itemView.findViewById(R.id.eventTimeTextView);
        }

        void bind(final Event event, final OnClickListener onClickListener) {
            eventTitleTextView.setText(event.title);
            timeTextView.setText(Utils.getTimes(event));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(event);
                }
            });
        }
    }

    private OnClickListener onClickListener;

    public ScheduleListAdapter(OnClickListener onClickListener) {
        super(DIFF_CALLBACK);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), onClickListener);
    }
}
