package com.cnt.police.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cnt.police.CriminalProfileActivity;
import com.cnt.police.R;
import com.cnt.police.models.Criminal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CriminalAdapter extends RecyclerView.Adapter<CriminalAdapter.CriminalHolder> {

    private final Context mContext;
    ArrayList<Criminal> mCriminals;

    public CriminalAdapter(Context context, ArrayList<Criminal> criminals) {
        mContext = context;
        mCriminals = criminals;
    }


    @NonNull
    @Override
    public CriminalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CriminalHolder(LayoutInflater.from(mContext).inflate(R.layout.item_criminal_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CriminalHolder holder, int position) {
        Glide.with(mContext)
                .load(mCriminals.get(position).getProfile_pic_url())
                .placeholder(R.drawable.ic_police_svg_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
                .into(holder.imgCriminalPic);
        holder.txtCriminalName.setText(mCriminals.get(position).getCriminal_name());
        try {
            holder.txtCriminalAge.setText(getAge(mCriminals.get(position).getDob()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txtCriminalAppearance.setText(mCriminals.get(position).getAppearance());
        holder.txtCriminalAddress.setText(mCriminals.get(position).getCriminal_address());
    }

    @Override
    public int getItemCount() {
        return mCriminals.size();
    }

    private String getAge(String dob) throws ParseException {
        Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dob);
        Calendar today = Calendar.getInstance();
        Calendar c_dob = Calendar.getInstance();
        if (date == null)
            return "NA";
        c_dob.setTime(date);
        int age = today.get(Calendar.YEAR) - c_dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < c_dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return String.valueOf(age).concat(" years");
    }

    public class CriminalHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCriminalPic;
        private final TextView txtCriminalName, txtCriminalAge, txtCriminalAppearance, txtCriminalAddress;

        public CriminalHolder(@NonNull View itemView) {
            super(itemView);
            imgCriminalPic = itemView.findViewById(R.id.item_CriminalPic);
            txtCriminalName = itemView.findViewById(R.id.item_CriminalName);
            txtCriminalAge = itemView.findViewById(R.id.item_CriminalAge);
            txtCriminalAppearance = itemView.findViewById(R.id.item_CriminalAppearance);
            txtCriminalAddress = itemView.findViewById(R.id.item_CriminalAddress);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, CriminalProfileActivity.class);
                intent.putExtra("criminal", mCriminals.get(getLayoutPosition()));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) mContext, imgCriminalPic, Objects.requireNonNull(ViewCompat.getTransitionName(imgCriminalPic))
                );
                ((Activity) mContext).startActivity(intent, options.toBundle());
            });
        }
    }


}
