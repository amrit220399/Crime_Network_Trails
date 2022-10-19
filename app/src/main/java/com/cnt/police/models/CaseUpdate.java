package com.cnt.police.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class CaseUpdate implements Parcelable {
    public static final Creator<CaseUpdate> CREATOR = new Creator<CaseUpdate>() {
        @Override
        public CaseUpdate createFromParcel(Parcel in) {
            return new CaseUpdate(in);
        }

        @Override
        public CaseUpdate[] newArray(int size) {
            return new CaseUpdate[size];
        }
    };
    private String updateTitle;
    private String updateDetails;
    private Timestamp updateTimestamp;

    public CaseUpdate() {

    }

    protected CaseUpdate(Parcel in) {
        updateTitle = in.readString();
        updateDetails = in.readString();
        updateTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getUpdateDetails() {
        return updateDetails;
    }

    public void setUpdateDetails(String updateDetails) {
        this.updateDetails = updateDetails;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    @Override
    public String toString() {
        return "CaseUpdate{" +
                "updateTitle='" + updateTitle + '\'' +
                ", updateDetails='" + updateDetails + '\'' +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updateTitle);
        dest.writeString(updateDetails);
        dest.writeParcelable(updateTimestamp, flags);
    }
}
