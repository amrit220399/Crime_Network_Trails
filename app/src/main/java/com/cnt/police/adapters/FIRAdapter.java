package com.cnt.police.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.models.FIR;
import com.cnt.police.status.AppointmentStatus;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FIRAdapter extends RecyclerView.Adapter<FIRAdapter.FIRHolder> {

    private final Context context;
    private ArrayList<FIR> mFIRArrayList;

    public FIRAdapter(Context context, ArrayList<FIR> FIRArrayList) {
        this.context = context;
        mFIRArrayList = FIRArrayList;
    }

    @NonNull
    @Override
    public FIRHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FIRHolder(LayoutInflater.from(context).inflate(R.layout.item_fir_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FIRHolder holder, int position) {
        holder.txtFIR_ID.setText("#".concat(mFIRArrayList.get(position).getFIR_ID()));
        holder.txtAccusedName.setText(mFIRArrayList.get(position).getAccusedName());
        holder.txtCrimeType.setText(mFIRArrayList.get(position).getCrime_type());
        holder.txtCrimeDetails.setText(mFIRArrayList.get(position).getCrime_details());
        holder.txtCrimeTime.setText(mFIRArrayList.get(position).getCrime_date().concat(" at ")
                .concat(mFIRArrayList.get(position).getCrime_time()));
        holder.txtApplicantName.setText(mFIRArrayList.get(position).getApplicantName());
        holder.txtApplicantPhone.setText(mFIRArrayList.get(position).getApplicantPhoneNumber());
        holder.txtStatus.setText(mFIRArrayList.get(position).getStatus());
        holder.txtComplainantName.setText(mFIRArrayList.get(position).getComplainantName());
        holder.txtAddress.setText(mFIRArrayList.get(position).getApplicantAddress());
        holder.txtCrimePlace.setText(mFIRArrayList.get(position).getCrime_place());
        holder.txtTimestamp.setText(mFIRArrayList.get(position).getCreated_at().toDate().toString());
        if (mFIRArrayList.get(position).getStatus().equals(AppointmentStatus.PENDING.name())) {
            holder.txtStatus.setVisibility(View.GONE);
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.txtStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mFIRArrayList.size();
    }

    public class FIRHolder extends RecyclerView.ViewHolder {
        private final MaterialButton btnReject, btnAccept;
        private final TextView txtFIR_ID, txtAccusedName, txtCrimeType, txtCrimeDetails, txtCrimeTime,
                txtApplicantName, txtApplicantPhone, txtStatus, txtComplainantName, txtAddress,
                txtTimestamp, txtCrimePlace;
        private final LinearLayout LL;
        private final TableLayout TL;
        private final ImageView imgArr;
        private boolean isDetailsOpened = false;

        public FIRHolder(@NonNull View itemView) {
            super(itemView);
            btnAccept = itemView.findViewById(R.id.item_FIR_btnAccept);
            btnReject = itemView.findViewById(R.id.item_FIR_btnReject);
            txtFIR_ID = itemView.findViewById(R.id.item_txtFIR_ID);
            txtAccusedName = itemView.findViewById(R.id.item_FIR_accusedName);
            txtCrimeType = itemView.findViewById(R.id.item_FIR_crimeType);
            txtCrimeDetails = itemView.findViewById(R.id.item_FIR_crimeDetails);
            txtCrimeTime = itemView.findViewById(R.id.item_FIR_crimeTime);
            txtApplicantName = itemView.findViewById(R.id.item_FIR_applicantName);
            txtApplicantPhone = itemView.findViewById(R.id.item_FIR_applicantPhone);
            txtStatus = itemView.findViewById(R.id.item_FIR_status);
            txtComplainantName = itemView.findViewById(R.id.item_FIR_complainantName);
            txtAddress = itemView.findViewById(R.id.item_FIR_address);
            txtTimestamp = itemView.findViewById(R.id.item_FIR_timestamp);
            txtCrimePlace = itemView.findViewById(R.id.item_FIR_crimePlace);
            LL = itemView.findViewById(R.id.LL_itemFIR_details);
            TL = itemView.findViewById(R.id.TL_itemFIR_details);
            imgArr = itemView.findViewById(R.id.item_FIR_imgArr);

            btnAccept.setOnClickListener(v -> {
                updateFIRStatus(AppointmentStatus.ACCEPTED.name());
            });
            btnReject.setOnClickListener(v -> {
                updateFIRStatus(AppointmentStatus.REJECTED.name());
            });

            LL.setOnClickListener(v -> {
                if (isDetailsOpened) {
                    imgArr.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    TL.setVisibility(View.GONE);
                } else {
                    imgArr.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    TL.setVisibility(View.VISIBLE);
                }
                isDetailsOpened = !isDetailsOpened;
            });
        }

        private void updateFIRStatus(String status) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("FIR")
                    .document(mFIRArrayList.get(getLayoutPosition()).getFIR_ID())
                    .update("status", status);

        }
    }
}
