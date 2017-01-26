package com.hubesco.software.walkingdog.profile.api;

/**
 *
 * @author paoesco
 */
public enum EventBusEndpoint {

    PROFILE_REPOSITORY("walkingdog.profiel.repository");

    private final String address;

    private EventBusEndpoint(String address) {
        this.address = address;
    }

    public String address() {
        return this.address;
    }

}
