package com.cnt.police.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class ReportedCriminal {
    private String criminalID;
    private String criminalName;
    private String criminalImgUrl;
    private String seenDate;
    private String seenTime;
    private String seenCity;
    private String seenPlace;
    private String details;
    private GeoPoint reportLocation;
    private Timestamp timestamp;

    public String getCriminalID() {
        return criminalID;
    }

    public void setCriminalID(String criminalID) {
        this.criminalID = criminalID;
    }

    public String getCriminalName() {
        return criminalName;
    }

    public void setCriminalName(String criminalName) {
        this.criminalName = criminalName;
    }

    public String getCriminalImgUrl() {
        return criminalImgUrl;
    }

    public void setCriminalImgUrl(String criminalImgUrl) {
        this.criminalImgUrl = criminalImgUrl;
    }

    public String getSeenDate() {
        return seenDate;
    }

    public void setSeenDate(String seenDate) {
        this.seenDate = seenDate;
    }

    public String getSeenTime() {
        return seenTime;
    }

    public void setSeenTime(String seenTime) {
        this.seenTime = seenTime;
    }

    public String getSeenCity() {
        return seenCity;
    }

    public void setSeenCity(String seenCity) {
        this.seenCity = seenCity;
    }

    public String getSeenPlace() {
        return seenPlace;
    }

    public void setSeenPlace(String seenPlace) {
        this.seenPlace = seenPlace;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public GeoPoint getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(GeoPoint reportLocation) {
        this.reportLocation = reportLocation;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
