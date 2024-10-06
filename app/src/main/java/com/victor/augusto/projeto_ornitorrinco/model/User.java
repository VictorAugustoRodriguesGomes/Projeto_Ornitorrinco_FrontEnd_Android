package com.victor.augusto.projeto_ornitorrinco.model;

public class User {
    public String uid;
    public String displayName;
    public String email;
    public String password;
    public String photo;
    public String emailVerification;
    public String codeVerification;

    public User(String uid, String codeVerification, String emailVerification, String photo, String password, String email, String displayName) {
        this.uid = uid;
        this.codeVerification = codeVerification;
        this.emailVerification = emailVerification;
        this.photo = photo;
        this.password = password;
        this.email = email;
        this.displayName = displayName;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCodeVerification() {
        return codeVerification;
    }

    public void setCodeVerification(String codeVerification) {
        this.codeVerification = codeVerification;
    }

    public String getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(String emailVerification) {
        this.emailVerification = emailVerification;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
