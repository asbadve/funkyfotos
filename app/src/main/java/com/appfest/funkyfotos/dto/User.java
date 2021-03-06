package com.appfest.funkyfotos.dto;

/**
 * Created by kaushald on 25/01/17.
 */

public class User {

    private String name;
    private String username;
    private String password;
    private String picName;
    private String relationShip;
    private boolean admin;
    private String fcmToken;
    private String firebaseUID;
    private String firebasePicUrl;
    private String userEmail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirebasePicUrl() {
        return firebasePicUrl;
    }

    public void setFirebasePicUrl(String firebasePicUrl) {
        this.firebasePicUrl = firebasePicUrl;
    }
}
