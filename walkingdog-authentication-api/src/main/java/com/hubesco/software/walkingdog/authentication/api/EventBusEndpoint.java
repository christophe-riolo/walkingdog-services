package com.hubesco.software.walkingdog.authentication.api;

/**
 *
 * @author paoesco
 */
public enum EventBusEndpoint {

    AUTHENTICATION_JWT("walkingdog.authentication.jwt"),
    AUTHENTICATION_DB("walkingdog.authentication.db");

    private final String address;

    private EventBusEndpoint(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
