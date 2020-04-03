package com.n8yn8.abma.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Sponsor;

/**
 * Created by Nate on 3/15/15.
 */
public class ImageAdapter extends ListAdapter<Sponsor, ImageAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Sponsor> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Sponsor>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Sponsor oldSponsor, @NonNull Sponsor newSponsor) {
                    return oldSponsor.id.equals(newSponsor.id);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Sponsor oldSponsor, @NonNull Sponsor newSponsor) {
                    return oldSponsor.imageUrl.equals(newSponsor.imageUrl);
                }
            };
    private OnClickListener onClickListener;

    public ImageAdapter(Context c, OnClickListener onClickListener) {
        super(DIFF_CALLBACK);

        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_sponsor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(getItem(position), onClickListener);
    }

    public interface OnClickListener {
        void onClick(Sponsor sponsor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView networkImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            networkImageView = itemView.findViewById(R.id.sponsorImageView);
        }

        void bindView(final Sponsor sponsor, final OnClickListener onClickListener) {
            Glide.with(networkImageView).load(sponsor.imageUrl).placeholder(R.drawable.abma_logo).into(networkImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(sponsor);
                }
            });
        }
    }
}
