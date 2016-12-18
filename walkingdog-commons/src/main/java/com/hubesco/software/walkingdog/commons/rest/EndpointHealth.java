package com.hubesco.software.walkingdog.commons.rest;

/**
 * @author paoesco
 */
public class EndpointHealth {

    private EndpointStatus status;

    /**
     * vertx json.
     */
    public EndpointHealth() {
    }

    public EndpointHealth(EndpointStatus status) {
        this.status = status;
    }

    public EndpointStatus getStatus() {
        return status;
    }

}
