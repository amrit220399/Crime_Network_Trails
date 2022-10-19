package com.cnt.police.models;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class NOC {
    private String nocID;
    private String surname;
    private String name;
    private String homeAddress;
    private String presentAddress;
    private String dateOfBirth;
    private String placeOfBirth;
    private String fatherName;
    private String motherName;
    private boolean haveCrimeCharges;
    private String identificationMark;
    private String occupation;
    private String userID;
    private String phoneNumber;
    private String nocType;
    private String status;
    private String police_station_id;
    private Timestamp created_at;

    public String getNocID() {
        return nocID;
    }

    public void setNocID(String nocID) {
        this.nocID = nocID;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public boolean isHaveCrimeCharges() {
        return haveCrimeCharges;
    }

    public void setHaveCrimeCharges(boolean haveCrimeCharges) {
        this.haveCrimeCharges = haveCrimeCharges;
    }

    public String getIdentificationMark() {
        return identificationMark;
    }

    public void setIdentificationMark(String identificationMark) {
        this.identificationMark = identificationMark;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNocType() {
        return nocType;
    }

    public void setNocType(String nocType) {
        this.nocType = nocType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPolice_station_id() {
        return police_station_id;
    }

    public void setPolice_station_id(String police_station_id) {
        this.police_station_id = police_station_id;
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
        NOC noc = (NOC) o;
        return nocID.equals(noc.nocID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nocID);
    }
}
