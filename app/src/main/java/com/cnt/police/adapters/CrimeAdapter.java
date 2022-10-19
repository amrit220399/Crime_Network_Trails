package com.cnt.police.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.models.Crime;

import java.util.ArrayList;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.CrimeHolder> {

    Context context;
    ArrayList<Crime> mCrimeArrayList;

    public CrimeAdapter(Context context, ArrayList<Crime> crimeArrayList) {
        this.context = context;
        mCrimeArrayList = crimeArrayList;
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CrimeHolder(LayoutInflater.from(context).inflate(R.layout.item_crime_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
        Log.i("TAG ADAPTER", "onBindViewHolder: " + mCrimeArrayList.get(position));
        holder.txtCrimeID.setText("#".concat(mCrimeArrayList.get(position).getCrime_id()));
        holder.txtCrimeType.setText(mCrimeArrayList.get(position).getCrime_type());
        holder.txtCrimeDetails.setText(mCrimeArrayList.get(position).getCrime_details());
        holder.txtCrimePlace.setText(mCrimeArrayList.get(position).getAddress_of_crime());
        holder.txtCrimeDistrict.setText(mCrimeArrayList.get(position).getCrime_district());
        holder.txtCrimeState.setText(mCrimeArrayList.get(position).getCrime_state());
        holder.txtCrimeStatus.setText(mCrimeArrayList.get(position).getCase_status());
        holder.txtCrimeDate.setText(mCrimeArrayList.get(position).getDate_of_crime());
        holder.mRatingBar.setRating(mCrimeArrayList.get(position).getCrime_rating());
    }

    @Override
    public int getItemCount() {
        return mCrimeArrayList.size();
    }

    public static class CrimeHolder extends RecyclerView.ViewHolder {
        private final TextView txtCrimeID, txtCrimeType, txtCrimeDetails, txtCrimePlace, txtCrimeDistrict,
                txtCrimeState, txtCrimeStatus, txtCrimeDate;
        private final RatingBar mRatingBar;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            txtCrimeID = itemView.findViewById(R.id.item_crime_ID);
            txtCrimeType = itemView.findViewById(R.id.item_crime_type);
            txtCrimeDetails = itemView.findViewById(R.id.item_crime_details);
            txtCrimePlace = itemView.findViewById(R.id.item_crime_place);
            txtCrimeDistrict = itemView.findViewById(R.id.item_crime_district);
            txtCrimeState = itemView.findViewById(R.id.item_crime_state);
            txtCrimeStatus = itemView.findViewById(R.id.item_crime_status);
            txtCrimeDate = itemView.findViewById(R.id.item_crime_date);
            mRatingBar = itemView.findViewById(R.id.item_crime_rating);
        }
    }
}
