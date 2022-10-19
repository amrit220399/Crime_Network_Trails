package com.cnt.police.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.models.CaseUpdate;

import java.util.ArrayList;

public class CaseUpdatesAdapter extends RecyclerView.Adapter<CaseUpdatesAdapter.CaseUpdatesHolder> {

    private final Context context;
    private ArrayList<CaseUpdate> mCaseUpdates;

    public CaseUpdatesAdapter(Context context, ArrayList<CaseUpdate> caseUpdates) {
        this.context = context;
        mCaseUpdates = caseUpdates;
    }

    @NonNull
    @Override
    public CaseUpdatesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaseUpdatesHolder(LayoutInflater.from(context).inflate(R.layout.item_case_update_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CaseUpdatesHolder holder, int position) {
        holder.txtUpdateTitle.setText(mCaseUpdates.get(position).getUpdateTitle());
        holder.txtUpdateDetails.setText(mCaseUpdates.get(position).getUpdateDetails());
        holder.txtUpdateTime.setText(mCaseUpdates.get(position).getUpdateTimestamp().toDate().toString());
    }

    @Override
    public int getItemCount() {
        return mCaseUpdates.size();
    }

    public static class CaseUpdatesHolder extends RecyclerView.ViewHolder {
        private final TextView txtUpdateTitle, txtUpdateDetails, txtUpdateTime;

        public CaseUpdatesHolder(@NonNull View itemView) {
            super(itemView);
            txtUpdateTitle = itemView.findViewById(R.id.caseUpdateHeading);
            txtUpdateDetails = itemView.findViewById(R.id.caseUpdateDetails);
            txtUpdateTime = itemView.findViewById(R.id.caseUpdateTimestamp);
        }
    }
}
