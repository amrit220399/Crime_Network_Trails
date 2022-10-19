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

public class TeamOfficerAdapter extends RecyclerView.Adapter<TeamOfficerAdapter.TeamOfficerHolder> {

    private final Context context;
    private ArrayList<PoliceUser> mPoliceUsers;

    public TeamOfficerAdapter(Context context, ArrayList<PoliceUser> policeUsers) {
        this.context = context;
        mPoliceUsers = policeUsers;
    }

    @NonNull
    @Override
    public TeamOfficerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeamOfficerHolder(LayoutInflater.from(context).inflate(R.layout.item_team_officer_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TeamOfficerHolder holder, int position) {
        holder.txtTeamOfficerName.setText(mPoliceUsers.get(position).getPolice_name());
        holder.txtTeamOfficerDesignation.setText(mPoliceUsers.get(position).getDesignation());
        holder.txtTeamOfficerPS.setText(mPoliceUsers.get(position).getPolice_station_id());
        holder.txtTeamOfficerMobile.setText(mPoliceUsers.get(position).getPhoneNumber());
        holder.txtTeamOfficerEmail.setText(mPoliceUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mPoliceUsers.size();
    }

    public class TeamOfficerHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCall, imgEmail;
        private final TextView txtTeamOfficerName, txtTeamOfficerDesignation, txtTeamOfficerPS, txtTeamOfficerMobile, txtTeamOfficerEmail;

        public TeamOfficerHolder(@NonNull View itemView) {
            super(itemView);
            imgCall = itemView.findViewById(R.id.imgCallTeamOfficer);
            imgEmail = itemView.findViewById(R.id.imgEmailTeamOfficer);
            txtTeamOfficerName = itemView.findViewById(R.id.item_TeamOfficerName);
            txtTeamOfficerDesignation = itemView.findViewById(R.id.item_TeamOfficerDesignation);
            txtTeamOfficerPS = itemView.findViewById(R.id.item_TeamOfficerPS);
            txtTeamOfficerMobile = itemView.findViewById(R.id.item_TeamOfficerMobile);
            txtTeamOfficerEmail = itemView.findViewById(R.id.item_TeamOfficerEmail);
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
