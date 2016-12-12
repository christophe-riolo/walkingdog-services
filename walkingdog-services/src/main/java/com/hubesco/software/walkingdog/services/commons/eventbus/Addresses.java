package com.hubesco.software.walkingdog.services.commons.eventbus;

/**
 *
 * @author paoesco
 */
public enum Addresses {

    LOCATION_DB("locationDb"),
    USER_DB("usersDb");

    private final String address;

    private Addresses(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
