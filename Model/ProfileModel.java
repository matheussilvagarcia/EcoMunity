package com.matheussilvagarcia.ecomunity.Model;

import android.net.Uri;

public class ProfileModel {

    String id, username, email, password, path;

    public ProfileModel(){}

    public ProfileModel(String id, String username, String email, String password, String path)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
