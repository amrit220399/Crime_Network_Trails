package com.cnt.police.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Criminal implements Parcelable {
    public static final Creator<Criminal> CREATOR = new Creator<Criminal>() {
        @Override
        public Criminal createFromParcel(Parcel in) {
            return new Criminal(in);
        }

        @Override
        public Criminal[] newArray(int size) {
            return new Criminal[size];
        }
    };
    private String criminal_id;
    private String criminal_name;
    private String dob;
    private String criminal_address;
    private String appearance;
    private String profile_pic_url;
    private String last_crime;
    private String criminal_adder_authority_id;
    private Timestamp created_at;
    private Timestamp updated_at;

    protected Criminal(Parcel in) {
        criminal_id = in.readString();
        criminal_name = in.readString();
        dob = in.readString();
        criminal_address = in.readString();
        appearance = in.readString();
        profile_pic_url = in.readString();
        last_crime = in.readString();
        criminal_adder_authority_id = in.readString();
        created_at = in.readParcelable(Timestamp.class.getClassLoader());
        updated_at = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public Criminal() {

    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public String getCriminal_id() {
        return criminal_id;
    }

    public void setCriminal_id(String criminal_id) {
        this.criminal_id = criminal_id;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getLast_crime() {
        return last_crime;
    }

    public void setLast_crime(String last_crime) {
        this.last_crime = last_crime;
    }

    public String getCriminal_adder_authority_id() {
        return criminal_adder_authority_id;
    }

    public void setCriminal_adder_authority_id(String criminal_adder_authority_id) {
        this.criminal_adder_authority_id = criminal_adder_authority_id;
    }

    public String getCriminal_name() {
        return criminal_name;
    }

    public void setCriminal_name(String criminal_name) {
        this.criminal_name = criminal_name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCriminal_address() {
        return criminal_address;
    }

    public void setCriminal_address(String criminal_address) {
        this.criminal_address = criminal_address;
    }


    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(criminal_id);
        dest.writeString(criminal_name);
        dest.writeString(dob);
        dest.writeString(criminal_address);
        dest.writeString(appearance);
        dest.writeString(profile_pic_url);
        dest.writeString(last_crime);
        dest.writeString(criminal_adder_authority_id);
        dest.writeParcelable(created_at, flags);
        dest.writeParcelable(updated_at, flags);
    }
}
