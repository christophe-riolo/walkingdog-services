package com.hubesco.software.walkingdog.api.location;

/**
 *
 * @author paoesco
 */
public class UserLocation {

    private String id;
    private double longitude;
    private double latitude;

    /**
     * Vertx Json.
     */
    public UserLocation() {
    }

    public UserLocation(String id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}
