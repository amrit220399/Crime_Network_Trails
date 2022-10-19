package com.cnt.police.models;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class FIR {
    private String FIR_ID;
    private String complainantName;
    private String complainantID;
    private String createdBy;
    private String policeStation;
    private String PS_district;
    private String PS_state;
    private String accusedName;
    private String applicantName;
    private String applicantAddress;
    private String applicantPhoneNumber;
    private String crime_type;
    private String crime_details;
    private String crime_date;
    private String crime_time;
    private String crime_place;
    private String status;
    private Timestamp created_at;

    public String getFIR_ID() {
        return FIR_ID;
    }

    public void setFIR_ID(String FIR_ID) {
        this.FIR_ID = FIR_ID;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public String getComplainantID() {
        return complainantID;
    }

    public void setComplainantID(String complainantID) {
        this.complainantID = complainantID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getPS_district() {
        return PS_district;
    }

    public void setPS_district(String PS_district) {
        this.PS_district = PS_district;
    }

    public String getPS_state() {
        return PS_state;
    }

    public void setPS_state(String PS_state) {
        this.PS_state = PS_state;
    }

    public String getAccusedName() {
        return accusedName;
    }

    public void setAccusedName(String accusedName) {
        this.accusedName = accusedName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantAddress() {
        return applicantAddress;
    }

    public void setApplicantAddress(String applicantAddress) {
        this.applicantAddress = applicantAddress;
    }

    public String getApplicantPhoneNumber() {
        return applicantPhoneNumber;
    }

    public void setApplicantPhoneNumber(String applicantPhoneNumber) {
        this.applicantPhoneNumber = applicantPhoneNumber;
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

    public String getCrime_date() {
        return crime_date;
    }

    public void setCrime_date(String crime_date) {
        this.crime_date = crime_date;
    }

    public String getCrime_time() {
        return crime_time;
    }

    public void setCrime_time(String crime_time) {
        this.crime_time = crime_time;
    }

    public String getCrime_place() {
        return crime_place;
    }

    public void setCrime_place(String crime_place) {
        this.crime_place = crime_place;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FIR fir = (FIR) o;
        return FIR_ID.equals(fir.FIR_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(FIR_ID);
    }
}
