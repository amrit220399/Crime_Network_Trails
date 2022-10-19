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
import com.cnt.police.models.NOC;
import com.cnt.police.status.AppointmentStatus;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NOCAdapter extends RecyclerView.Adapter<NOCAdapter.NOCHolder> {

    private final Context context;
    private ArrayList<NOC> mNOCArrayList;

    public NOCAdapter(Context context, ArrayList<NOC> NOCArrayList) {
        this.context = context;
        mNOCArrayList = NOCArrayList;
    }

    @NonNull
    @Override
    public NOCHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NOCHolder(LayoutInflater.from(context).inflate(R.layout.item_noc_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NOCHolder holder, int position) {
        holder.txtNocId.setText("#".concat(mNOCArrayList.get(position).getNocID()));
        holder.txtFullName.setText(mNOCArrayList.get(position).getName().concat(" ")
                .concat(mNOCArrayList.get(position).getSurname()));
        holder.txtDobPlace.setText(mNOCArrayList.get(position).getDateOfBirth().concat(" | ")
                .concat(mNOCArrayList.get(position).getPlaceOfBirth()));
        holder.txtPhoneNumber.setText(mNOCArrayList.get(position).getPhoneNumber());
        holder.txtOccupation.setText(mNOCArrayList.get(position).getOccupation());
        holder.txtAddress.setText(mNOCArrayList.get(position).getPresentAddress());
        holder.txtNocType.setText(mNOCArrayList.get(position).getNocType());
        holder.txtStatus.setText(mNOCArrayList.get(position).getStatus());
        holder.txtFatherName.setText(mNOCArrayList.get(position).getFatherName());
        holder.txtMotherName.setText(mNOCArrayList.get(position).getMotherName());
        holder.txtMark.setText(mNOCArrayList.get(position).getIdentificationMark());
        if (mNOCArrayList.get(position).isHaveCrimeCharges()) {
            holder.txtCrimeCharges.setText("YES");
        } else {
            holder.txtCrimeCharges.setText("NO");
        }
        holder.txtTimestamp.setText(mNOCArrayList.get(position).getCreated_at().toDate().toString());
        if (mNOCArrayList.get(position).getStatus().equals(AppointmentStatus.PENDING.name())) {
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
        return mNOCArrayList.size();
    }

    public class NOCHolder extends RecyclerView.ViewHolder {
        private final TextView txtNocId, txtFullName, txtDobPlace, txtPhoneNumber, txtOccupation,
                txtAddress, txtNocType, txtStatus, txtFatherName, txtMotherName, txtMark, txtCrimeCharges, txtTimestamp;
        private final MaterialButton btnAccept, btnReject;
        private final ImageView imgArr;
        private final TableLayout TL;
        private final LinearLayout LL;
        private boolean isDetailsOpened = false;

        public NOCHolder(@NonNull View itemView) {
            super(itemView);
            txtNocId = itemView.findViewById(R.id.item_txtNOC_ID);
            txtFullName = itemView.findViewById(R.id.item_NOC_fullName);
            txtDobPlace = itemView.findViewById(R.id.item_NOC_dob_place);
            txtPhoneNumber = itemView.findViewById(R.id.item_NOC_phoneNumber);
            txtOccupation = itemView.findViewById(R.id.item_NOC_occupation);
            txtAddress = itemView.findViewById(R.id.item_NOC_address);
            txtNocType = itemView.findViewById(R.id.item_NOC_nocType);
            txtStatus = itemView.findViewById(R.id.item_NOC_status);
            btnAccept = itemView.findViewById(R.id.item_NOC_btnAccept);
            btnReject = itemView.findViewById(R.id.item_NOC_btnReject);
            imgArr = itemView.findViewById(R.id.item_NOC_imgArr);
            txtFatherName = itemView.findViewById(R.id.item_NOC_fatherName);
            txtMotherName = itemView.findViewById(R.id.item_NOC_motherName);
            txtMark = itemView.findViewById(R.id.item_NOC_identificationMark);
            txtCrimeCharges = itemView.findViewById(R.id.item_NOC_CrimeCharges);
            txtTimestamp = itemView.findViewById(R.id.item_NOC_timestamp);
            TL = itemView.findViewById(R.id.TL_itemNOC_details);
            LL = itemView.findViewById(R.id.LL_itemNOC_details);

            btnAccept.setOnClickListener(v -> {
                updateNOCStatus(AppointmentStatus.ACCEPTED.name());
            });
            btnReject.setOnClickListener(v -> {
                updateNOCStatus(AppointmentStatus.REJECTED.name());
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

        private void updateNOCStatus(String status) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("NOC")
                    .document(mNOCArrayList.get(getLayoutPosition()).getNocID())
                    .update("status", status);

        }
    }
}
