package com.hubesco.software.walkingdog.commons;

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

    public static String sendGridApiKey() {
        String property = System.getenv("SENDGRID_API_KEY");
        if (property == null) {
            property = System.getProperty("SENDGRID_API_KEY");
        }
        return property;
    }
    
    public static String walkingdogAuthenticationApiUrl() {
        String property = System.getenv("WALKINGDOG_AUTHENTICATION_API_URL");
        if (property == null) {
            property = System.getProperty("WALKINGDOG_AUTHENTICATION_API_URL");
        }
        return property;
    }

}
