package com.cnt.police.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Crime implements Parcelable {
    private String crime_id;
    private String crime_type;
    private String crime_details;
    private String crime_state;
    private String crime_district;
    private String date_of_crime;
    private String address_of_crime;
    private float crime_rating;
    private String case_status;
    private String crime_adder_authority_id;
    private String criminal_id;
    private Timestamp created_at;
    private Timestamp updated_at;
    public static final Creator<Crime> CREATOR = new Creator<Crime>() {
        @Override
        public Crime createFromParcel(Parcel in) {
            return new Crime(in);
        }

        @Override
        public Crime[] newArray(int size) {
            return new Crime[size];
        }
    };
    private ArrayList<CaseUpdate> caseUpdates;

    protected Crime(Parcel in) {
        crime_id = in.readString();
        crime_type = in.readString();
        crime_details = in.readString();
        crime_state = in.readString();
        crime_district = in.readString();
        date_of_crime = in.readString();
        address_of_crime = in.readString();
        crime_rating = in.readFloat();
        case_status = in.readString();
        crime_adder_authority_id = in.readString();
        criminal_id = in.readString();
        created_at = in.readParcelable(Timestamp.class.getClassLoader());
        updated_at = in.readParcelable(Timestamp.class.getClassLoader());
        caseUpdates = in.createTypedArrayList(CaseUpdate.CREATOR);
    }

    public Crime() {
        //Empty Constructor
    }

    public ArrayList<CaseUpdate> getCaseUpdates() {
        return caseUpdates;
    }

    public void setCaseUpdates(ArrayList<CaseUpdate> caseUpdates) {
        this.caseUpdates = caseUpdates;
    }


    public String getCrime_id() {
        return crime_id;
    }

    public void setCrime_id(String crime_id) {
        this.crime_id = crime_id;
    }

    public String getCrime_type() {
        return crime_type;
    }

    public void setCrime_type(String crime_type) {
        this.crime_type = crime_type;
    }

    public String getCrime_details() {
        return crime_details;
    }

    public void setCrime_details(String crime_details) {
        this.crime_details = crime_details;
    }

    public String getCrime_state() {
        return crime_state;
    }

    public void setCrime_state(String crime_state) {
        this.crime_state = crime_state;
    }

    public String getCrime_district() {
        return crime_district;
    }

    public void setCrime_district(String crime_district) {
        this.crime_district = crime_district;
    }

    public String getDate_of_crime() {
        return date_of_crime;
    }

    public void setDate_of_crime(String date_of_crime) {
        this.date_of_crime = date_of_crime;
    }

    public String getAddress_of_crime() {
        return address_of_crime;
    }

    public void setAddress_of_crime(String address_of_crime) {
        this.address_of_crime = address_of_crime;
    }

    public float getCrime_rating() {
        return crime_rating;
    }

    public void setCrime_rating(float crime_rating) {
        this.crime_rating = crime_rating;
    }


    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

    public String getCrime_adder_authority_id() {
        return crime_adder_authority_id;
    }

    public void setCrime_adder_authority_id(String crime_adder_authority_id) {
        this.crime_adder_authority_id = crime_adder_authority_id;
    }

    public String getCriminal_id() {
        return criminal_id;
    }

    public void setCriminal_id(String criminal_id) {
        this.criminal_id = criminal_id;
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

        dest.writeString(crime_id);
        dest.writeString(crime_type);
        dest.writeString(crime_details);
        dest.writeString(crime_state);
        dest.writeString(crime_district);
        dest.writeString(date_of_crime);
        dest.writeString(address_of_crime);
        dest.writeFloat(crime_rating);
        dest.writeString(case_status);
        dest.writeString(crime_adder_authority_id);
        dest.writeString(criminal_id);
        dest.writeParcelable(created_at, flags);
        dest.writeParcelable(updated_at, flags);
        dest.writeTypedList(caseUpdates);
    }


    @Override
    public String toString() {
        return "Crime{" +
                "crime_id='" + crime_id + '\'' +
                ", crime_type='" + crime_type + '\'' +
                ", crime_details='" + crime_details + '\'' +
                ", crime_state='" + crime_state + '\'' +
                ", crime_district='" + crime_district + '\'' +
                ", date_of_crime='" + date_of_crime + '\'' +
                ", address_of_crime='" + address_of_crime + '\'' +
                ", crime_rating=" + crime_rating +
                ", case_status='" + case_status + '\'' +
                ", crime_adder_authority_id='" + crime_adder_authority_id + '\'' +
                ", criminal_id='" + criminal_id + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", caseUpdates=" + caseUpdates +
                '}';
    }
}
