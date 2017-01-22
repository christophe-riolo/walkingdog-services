package com.hubesco.software.walkingdog.location.api;

/**
 *
 * @author paoesco
 */
public enum EventBusEndpoint {

    LOCATION_DB("walkingdog.location.db");

    private final String address;

    private EventBusEndpoint(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
