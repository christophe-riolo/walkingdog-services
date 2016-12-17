package com.hubesco.software.walkingdog.services.commons;

/**
 * @author paoesco
 */
public abstract class EnvironmentProperties {

    private EnvironmentProperties() {

    }

    public static String jwtKeystorePassword() {
        String property = System.getenv("JWT_KEYSTORE_PASSWORD");
        if (property == null) {
            property = System.getProperty("JWT_KEYSTORE_PASSWORD");
        }
        return property;
    }

    public static String jwtKeystorePath() {
        String property = System.getenv("JWT_KEYSTORE_PATH");
        if (property == null) {
            property = System.getProperty("JWT_KEYSTORE_PATH");
        }
        return property;
    }

}
