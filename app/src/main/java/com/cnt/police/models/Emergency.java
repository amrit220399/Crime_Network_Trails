package com.cnt.police.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Emergency {
    public boolean isEmergency;
    private GeoPoint emergencyLocation;
    private String userType;
    private Timestamp timestamp;
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GeoPoint getEmergencyLocation() {
        return emergencyLocation;
    }

    public void setEmergencyLocation(GeoPoint emergencyLocation) {
        this.emergencyLocation = emergencyLocation;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
