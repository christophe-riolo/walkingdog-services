package com.hubesco.software.walkingdog.services.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author paoesco
 */
public class IonicUsersData {

    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

}
