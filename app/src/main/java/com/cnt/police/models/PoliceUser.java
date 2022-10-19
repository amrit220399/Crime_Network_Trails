package com.cnt.police.models;

public class PoliceUser {
    private String phoneNumber;
    private String email;
    private String police_name;
    private String police_station_id;
    private String posted_city;
    private String designation;
    private String posted_state;
    private boolean psHead;
    private int rating;
    private String notification_id;
    private Boolean isVerified;

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getPosted_state() {
        return posted_state;
    }

    public void setPosted_state(String posted_state) {
        this.posted_state = posted_state;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPolice_name() {
        return police_name;
    }

    public void setPolice_name(String police_name) {
        this.police_name = police_name;
    }

    public String getPolice_station_id() {
        return police_station_id;
    }

    public void setPolice_station_id(String police_station_id) {
        this.police_station_id = police_station_id;
    }

    public String getPosted_city() {
        return posted_city;
    }

    public void setPosted_city(String posted_city) {
        this.posted_city = posted_city;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isPsHead() {
        return psHead;
    }

    public void setPsHead(boolean psHead) {
        this.psHead = psHead;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }
}
