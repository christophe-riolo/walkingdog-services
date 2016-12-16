package com.hubesco.software.walkingdog.api.location;

/**
 * @author paoesco
 */
public class DogLocation {

    private String userUuid;
    private String dogName;
    private double longitude;
    private double latitude;
    private boolean walking;

    /**
     * vertx json.
     */
    public DogLocation() {
    }

    // create and initialize a point with given name and
    // (latitude, longitude) specified in degrees
    public DogLocation(String userUuid, String name, double latitude, double longitude, boolean walking) {
        this.userUuid = userUuid;
        this.dogName = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.walking = walking;
    }

    // return distance between this location and that location
    // measured in statute miles
    public double distanceTo(DogLocation that) {
        double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(that.latitude);
        double lon2 = Math.toRadians(that.longitude);

        // great circle distance in radians, using law of cosines formula
        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        // each degree on a great circle of Earth is 60 nautical miles
        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

    // return string representation of this point
    @Override
    public String toString() {
        return dogName + " (" + latitude + ", " + longitude + ")";
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getDogName() {
        return dogName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isWalking() {
        return walking;
    }

}
