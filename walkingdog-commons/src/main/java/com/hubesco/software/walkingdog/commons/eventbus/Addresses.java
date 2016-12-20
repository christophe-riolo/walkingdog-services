package com.hubesco.software.walkingdog.commons.eventbus;

/**
 *
 * @author paoesco
 */
public enum Addresses {

    LOCATION_DB("walkingdog.location.db"),
    AUTHENTICATION_JWT("walkingdog.authentication.jwt"),
    AUTHENTICATION_DB("walkingdog.authentication.db"),
    EMAIL_SERVICES("walkingdog.email");

    private final String address;

    private Addresses(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
