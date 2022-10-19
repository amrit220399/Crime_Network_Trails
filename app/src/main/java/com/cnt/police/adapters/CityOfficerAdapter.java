package com.cnt.police.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.models.PoliceUser;

import java.util.ArrayList;

public class CityOfficerAdapter extends RecyclerView.Adapter<CityOfficerAdapter.CityOfficerHolder> {

    private final Context context;
    private ArrayList<PoliceUser> mPoliceUsers;

    public CityOfficerAdapter(Context context, ArrayList<PoliceUser> policeUsers) {
        this.context = context;
        mPoliceUsers = policeUsers;
    }

    @NonNull
    @Override
    public CityOfficerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CityOfficerHolder(LayoutInflater.from(context).inflate(R.layout.item_city_officer_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CityOfficerHolder holder, int position) {
        holder.txtCityOfficerName.setText(mPoliceUsers.get(position).getPolice_name());
        holder.txtCityOfficerDesignation.setText(mPoliceUsers.get(position).getDesignation());
        holder.txtCityOfficerPS.setText(mPoliceUsers.get(position).getPolice_station_id());
        holder.txtCityOfficerMobile.setText(mPoliceUsers.get(position).getPhoneNumber());
        holder.txtCityOfficerEmail.setText(mPoliceUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mPoliceUsers.size();
    }

    public class CityOfficerHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCall, imgEmail;
        private final TextView txtCityOfficerName, txtCityOfficerDesignation, txtCityOfficerPS, txtCityOfficerMobile, txtCityOfficerEmail;

        public CityOfficerHolder(@NonNull View itemView) {
            super(itemView);
            imgCall = itemView.findViewById(R.id.imgCallCityOfficer);
            imgEmail = itemView.findViewById(R.id.imgEmailCityOfficer);
            txtCityOfficerName = itemView.findViewById(R.id.item_CityOfficerName);
            txtCityOfficerDesignation = itemView.findViewById(R.id.item_CityOfficerDesignation);
            txtCityOfficerPS = itemView.findViewById(R.id.item_CityOfficerPS);
            txtCityOfficerMobile = itemView.findViewById(R.id.item_CityOfficerMobile);
            txtCityOfficerEmail = itemView.findViewById(R.id.item_CityOfficerEmail);

            imgEmail.setOnClickListener(v -> {
                String email = mPoliceUsers.get(getLayoutPosition()).getEmail();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CNT POLICE OFFICER");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "I want to request/share...");
                if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(Intent.createChooser(emailIntent, "Choose email client:"));
                }
            });
            imgCall.setOnClickListener(v -> {
                String phone = mPoliceUsers.get(getLayoutPosition()).getPhoneNumber();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                if (callIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(Intent.createChooser(callIntent, "Choose dialer:"));
                }
            });
        }
    }
}
