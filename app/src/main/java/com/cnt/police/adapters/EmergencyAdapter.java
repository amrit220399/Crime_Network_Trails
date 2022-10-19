package com.cnt.police.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.models.Emergency;
import com.cnt.police.status.UserType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyHolder> {

    private Context context;
    private ArrayList<Emergency> mEmergencies;
    private Geocoder mGeocoder;

    public EmergencyAdapter(Context context, ArrayList<Emergency> emergencies) {
        this.context = context;
        mEmergencies = emergencies;
        mGeocoder = new Geocoder(context);
    }

    @NonNull
    @Override
    public EmergencyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EmergencyHolder(LayoutInflater.from(context).inflate(R.layout.item_emergency_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyHolder holder, int position) {
        if (mEmergencies.get(position).getUserType().equals(UserType.POLICE_USER.name())) {
            holder.txtEmergencyType.setText("Backup Needed");
        } else {
            holder.txtEmergencyType.setText("User Emergency");
        }
        try {
            List<Address> addressList = mGeocoder.getFromLocation(
                    mEmergencies.get(position).getEmergencyLocation().getLatitude(),
                    mEmergencies.get(position).getEmergencyLocation().getLongitude(),
                    1
            );
            Address address = addressList.get(0);
            holder.txtEmergencyAddress.setText(address.getAddressLine(address.getMaxAddressLineIndex()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mEmergencies.size();
    }

    public static class EmergencyHolder extends RecyclerView.ViewHolder {
        private final TextView txtEmergencyType, txtEmergencyAddress;

        public EmergencyHolder(@NonNull View itemView) {
            super(itemView);
            txtEmergencyType = itemView.findViewById(R.id.txtEmergencyType);
            txtEmergencyAddress = itemView.findViewById(R.id.txtEmergencyAddress);
        }
    }
}
