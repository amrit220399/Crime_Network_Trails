package com.cnt.police.models;

import com.google.firebase.Timestamp;

public class Feed {
    private String feedTitle;
    private String creatorID;
    private String creatorName;
    private String creatorDesignation;
    private String creatorLocation;
    private Timestamp timestamp;
    private String description;
    private String feedImgUrl;

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorDesignation() {
        return creatorDesignation;
    }

    public void setCreatorDesignation(String creatorDesignation) {
        this.creatorDesignation = creatorDesignation;
    }

    public String getCreatorLocation() {
        return creatorLocation;
    }

    public void setCreatorLocation(String creatorLocation) {
        this.creatorLocation = creatorLocation;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeedImgUrl() {
        return feedImgUrl;
    }

    public void setFeedImgUrl(String feedImgUrl) {
        this.feedImgUrl = feedImgUrl;
    }
}
