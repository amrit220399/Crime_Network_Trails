package com.cnt.police.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cnt.police.R;
import com.cnt.police.models.News;
import com.cnt.police.ui.fragments.FeedsFragmentDirections;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {
    private Context context;
    private ArrayList<News> mNewsArrayList;
    private SimpleDateFormat sdf_in, sdf_out;

    public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        this.context = context;
        mNewsArrayList = newsArrayList;
        sdf_in = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.US);
        sdf_in.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf_out = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aaa", Locale.getDefault());
        sdf_out.setTimeZone(TimeZone.getDefault());
    }

    public NewsAdapter() {
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsHolder(LayoutInflater.from(context).inflate(R.layout.item_news_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        Glide.with(context)
                .load(mNewsArrayList.get(position).getUrlToImage())
                .fitCenter()
                .placeholder(R.drawable.news_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.imgNews);
        holder.txtTitle.setText(mNewsArrayList.get(position).getTitle());
        holder.txtDesc.setText(mNewsArrayList.get(position).getDescription());
        Date date = null;
        try {
            date = sdf_in.parse(mNewsArrayList.get(position).getPublishedAt());
            holder.txtTimestamp.setText(sdf_out.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txtAuthor.setText(mNewsArrayList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return mNewsArrayList.size();
    }

    public class NewsHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle, txtDesc, txtAuthor, txtTimestamp;
        private ShapeableImageView imgNews;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNewsTitle);
            txtDesc = itemView.findViewById(R.id.txtNewsDesc);
            txtAuthor = itemView.findViewById(R.id.txtNewsAuthor);
            txtTimestamp = itemView.findViewById(R.id.txtNewsTimestamp);
            imgNews = itemView.findViewById(R.id.iv_news);

            itemView.setOnClickListener(v -> {
                FeedsFragmentDirections.ActionBottomNavFeedsToWebNewsActivity action =
                        FeedsFragmentDirections.actionBottomNavFeedsToWebNewsActivity();
                action.setWebUrl(mNewsArrayList.get(getLayoutPosition()).getUrl());
                Navigation.findNavController((Activity) context, R.id.nav_host_fragment)
                        .navigate(action);
//                Intent intent = new Intent(context, WebNewsActivity.class);
//                intent.putExtra("webUrl",mNewsArrayList.get(getLayoutPosition()).getUrl());
//                context.startActivity(intent);
            });
        }
    }
}
