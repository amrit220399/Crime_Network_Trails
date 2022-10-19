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
import com.cnt.police.models.ReportedCriminal;

import java.util.ArrayList;

public class ReportedCriminalsAdapter extends RecyclerView.Adapter<ReportedCriminalsAdapter.ReportedCriminalHolder> {

    private final Context context;
    private final ArrayList<ReportedCriminal> mReportedCriminals;

    public ReportedCriminalsAdapter(Context context, ArrayList<ReportedCriminal> reportedCriminals) {
        this.context = context;
        mReportedCriminals = reportedCriminals;
    }

    @NonNull
    @Override
    public ReportedCriminalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportedCriminalHolder(LayoutInflater.from(context).inflate(R.layout.item_reported_criminal_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedCriminalHolder holder, int position) {
        Glide.with(context)
                .load(mReportedCriminals.get(position).getCriminalImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_thief)
                .fitCenter()
                .into(holder.imgCriminal);
        holder.txtCriminalName.setText(mReportedCriminals.get(position).getCriminalName());
        holder.txtPlaceSeen.setText(mReportedCriminals.get(position).getSeenPlace());
        holder.txtDateTimeSeen.setText(mReportedCriminals.get(position).getSeenDate()
                .concat(" at ").concat(mReportedCriminals.get(position).getSeenTime()));
        holder.txtDetails.setText(mReportedCriminals.get(position).getDetails());
    }

    @Override
    public int getItemCount() {
        return mReportedCriminals.size();
    }

    public class ReportedCriminalHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCriminal;
        private final TextView txtCriminalName, txtPlaceSeen, txtDateTimeSeen, txtDetails;

        public ReportedCriminalHolder(@NonNull View itemView) {
            super(itemView);
            imgCriminal = itemView.findViewById(R.id.item_imgRCriminal);
            txtCriminalName = itemView.findViewById(R.id.item_RCriminal_name);
            txtPlaceSeen = itemView.findViewById(R.id.item_RCriminal_place);
            txtDateTimeSeen = itemView.findViewById(R.id.item_RCriminal_timestamp);
            txtDetails = itemView.findViewById(R.id.item_RCriminal_details);
        }
    }
}
