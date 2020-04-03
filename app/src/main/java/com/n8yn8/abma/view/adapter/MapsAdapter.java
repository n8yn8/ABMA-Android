package com.n8yn8.abma.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Map;

public class MapsAdapter extends ListAdapter<Map, MapsAdapter.MapViewHolder> {

    private static final DiffUtil.ItemCallback<Map> DIFF_CALLBACK = new DiffUtil.ItemCallback<Map>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Map oldItem, @NonNull Map newItem) {
                    return oldItem.id.equals(newItem.id);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Map oldItem, @NonNull Map newItem) {
                    return oldItem.url.equals(newItem.url);
                }
            };

    private OnMapClickListener onMapClickListener;

    public MapsAdapter(OnMapClickListener onMapClickListener) {
        super(DIFF_CALLBACK);
        this.onMapClickListener = onMapClickListener;
    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_map, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapViewHolder holder, int position) {
        holder.onBind(getItem(position), onMapClickListener);
    }

    public interface OnMapClickListener {
        void onClick(Map map);
    }

    static class MapViewHolder extends RecyclerView.ViewHolder {

        ImageView networkImageView;
        TextView titleTextView;

        MapViewHolder(View itemView) {
            super(itemView);

            networkImageView = itemView.findViewById(R.id.mapImageView);
            titleTextView = itemView.findViewById(R.id.textView4);
        }

        void onBind(final Map map, final OnMapClickListener onMapClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMapClickListener.onClick(map);
                }
            });

            Glide.with(networkImageView).load(map.url).placeholder(R.drawable.abma_logo).into(networkImageView);
            titleTextView.setText(map.title);
        }
    }
}
