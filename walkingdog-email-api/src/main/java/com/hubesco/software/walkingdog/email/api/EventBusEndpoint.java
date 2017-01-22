package com.hubesco.software.walkingdog.email.api;

/**
 *
 * @author paoesco
 */
public enum EventBusEndpoint {

    LOCATION_DB("walkingdog.location.db"),
    EMAIL_SERVICES("walkingdog.email");

    private final String address;

    private EventBusEndpoint(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
