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

import com.cnt.police.R;
import com.cnt.police.models.Crime;
import com.cnt.police.ui.fragments.HomeFragmentDirections;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseHolder> {

    Context mContext;
    ArrayList<Crime> mCrimes;

    public CaseAdapter(Context context, ArrayList<Crime> crimes) {
        mContext = context;
        mCrimes = crimes;
    }

    @NonNull
    @Override
    public CaseAdapter.CaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaseHolder(LayoutInflater.from(mContext).inflate(R.layout.item_case, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CaseAdapter.CaseHolder holder, int position) {
        holder.txtCaseType.setText(mCrimes.get(position).getCrime_type());
        holder.txtCaseDetails.setText(mCrimes.get(position).getCrime_details());
        holder.txtCaseDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(mCrimes.get(position).getCreated_at().toDate()));
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    public class CaseHolder extends RecyclerView.ViewHolder {
        private final TextView txtCaseType, txtCaseDetails, txtCaseDate;

        public CaseHolder(@NonNull View itemView) {
            super(itemView);
            txtCaseDate = itemView.findViewById(R.id.item_case_date);
            txtCaseDetails = itemView.findViewById(R.id.item_case_details);
            txtCaseType = itemView.findViewById(R.id.item_case_type);

            itemView.setOnClickListener(v -> {
                Navigation.findNavController((Activity) mContext, R.id.nav_host_fragment)
                        .navigate(HomeFragmentDirections.actionBottomNavHomeToCaseDetailsFragment(mCrimes.get(getLayoutPosition())));
            });
        }
    }
}
