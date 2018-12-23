package com.n8yn8.abma.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Map;

import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MapViewHolder> {

    private List<Map> maps;
    private OnMapClickListener onMapClickListener;
    private ImageLoader imageLoader;

    public MapsAdapter(ImageLoader imageLoader, List<Map> maps, OnMapClickListener onMapClickListener) {
        this.maps = maps;
        this.onMapClickListener = onMapClickListener;
        this.imageLoader = imageLoader;
    }

    @Override
    public MapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_map, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapViewHolder holder, int position) {
        holder.onBind(maps.get(position), onMapClickListener, imageLoader);
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    public interface OnMapClickListener {
        void onClick(Map map);
    }

    class MapViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView networkImageView;
        TextView titleTextView;

        MapViewHolder(View itemView) {
            super(itemView);

            networkImageView = itemView.findViewById(R.id.mapImageView);
            titleTextView = itemView.findViewById(R.id.textView4);
        }

        void onBind(final Map map, final OnMapClickListener onMapClickListener, ImageLoader imageLoader) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMapClickListener.onClick(map);
                }
            });

            networkImageView.setImageUrl(map.url, imageLoader);
            titleTextView.setText(map.title);
        }
    }
}
