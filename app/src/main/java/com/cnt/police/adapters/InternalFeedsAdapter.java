package com.cnt.police.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cnt.police.R;
import com.cnt.police.models.Feed;

import java.util.ArrayList;

public class InternalFeedsAdapter extends RecyclerView.Adapter<InternalFeedsAdapter.InternalFeedsHolder> {

    private final Context context;
    private final ArrayList<Feed> mFeeds;

    public InternalFeedsAdapter(Context context, ArrayList<Feed> feeds) {
        this.context = context;
        mFeeds = feeds;
    }

    @NonNull
    @Override
    public InternalFeedsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InternalFeedsHolder(LayoutInflater.from(context).inflate(R.layout.item_internal_updates_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InternalFeedsHolder holder, int position) {
        if (!mFeeds.get(position).getFeedImgUrl().isEmpty()) {
            Glide.with(context)
                    .load(mFeeds.get(position).getFeedImgUrl())
                    .fitCenter()
                    .placeholder(R.drawable.news_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imgFeed);
        }
        holder.txtFeedTitle.setText(mFeeds.get(position).getFeedTitle());
        holder.txtFeedCreator.setText(mFeeds.get(position).getCreatorName());
        holder.txtFeedDesignation.setText(mFeeds.get(position).getCreatorDesignation());
        holder.txtFeedTimestamp.setText(mFeeds.get(position).getTimestamp().toDate().toString());
        holder.txtFeedDescription.setText(mFeeds.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    public static class InternalFeedsHolder extends RecyclerView.ViewHolder {
        private final ImageView imgFeed;
        private final TextView txtFeedTitle, txtFeedCreator, txtFeedDesignation,
                txtFeedTimestamp, txtFeedDescription;

        public InternalFeedsHolder(@NonNull View itemView) {
            super(itemView);
            imgFeed = itemView.findViewById(R.id.item_imgFeed);
            txtFeedTitle = itemView.findViewById(R.id.item_feedTitle);
            txtFeedCreator = itemView.findViewById(R.id.item_feedCreator);
            txtFeedDesignation = itemView.findViewById(R.id.item_feedDesignation);
            txtFeedTimestamp = itemView.findViewById(R.id.item_feedTimeStamp);
            txtFeedDescription = itemView.findViewById(R.id.item_feedDescription);
        }
    }
}
