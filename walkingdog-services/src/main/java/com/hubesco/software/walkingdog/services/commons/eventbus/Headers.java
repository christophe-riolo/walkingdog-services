package com.hubesco.software.walkingdog.services.commons.eventbus;

/**
 *
 * @author paoesco
 */
public enum Headers {

    COMMAND("coommand");

    private final String header;

    private Headers(String header) {
        this.header = header;
    }

    public String header() {
        return this.header;
    }
    
}
