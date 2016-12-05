package com.hubesco.software.walkingdog.services.common.eventbus;

/**
 *
 * @author paoesco
 */
public enum Addresses {

    LOCATION_DB("locationDb");

    private final String address;

    private Addresses(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
