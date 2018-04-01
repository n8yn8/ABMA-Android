package com.n8yn8.abma.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BMap;

import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MapViewHolder> {

    private List<BMap> maps;
    private OnMapClickListener onMapClickListener;
    private ImageLoader mImageLoader;

    public MapsAdapter(Context context, List<BMap> maps, OnMapClickListener onMapClickListener) {
        this.maps = maps;
        this.onMapClickListener = onMapClickListener;

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(4 * 1024 * 1024); //4MB
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    @Override
    public MapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_map, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapViewHolder holder, int position) {
        holder.onBind(maps.get(position), onMapClickListener, mImageLoader);
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    public interface OnMapClickListener {
        void onClick(BMap map);
    }

    class MapViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView networkImageView;
        TextView titleTextView;

        MapViewHolder(View itemView) {
            super(itemView);

            networkImageView = (NetworkImageView) itemView.findViewById(R.id.mapImageView);
            titleTextView = (TextView) itemView.findViewById(R.id.textView4);
        }

        void onBind(final BMap map, final OnMapClickListener onMapClickListener, ImageLoader imageLoader) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMapClickListener.onClick(map);
                }
            });

            networkImageView.setImageUrl(map.getUrl(), imageLoader);
            titleTextView.setText(map.getTitle());
        }
    }
}
