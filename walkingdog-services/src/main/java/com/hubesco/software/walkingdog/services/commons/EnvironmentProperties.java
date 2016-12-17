package com.hubesco.software.walkingdog.services.commons;

/**
 * @author paoesco
 */
public abstract class EnvironmentProperties {

    private EnvironmentProperties() {

    }

    public static String jwtKeystoreKeypass() {
        String property = System.getenv("JWT_KEYSTORE_KEYPASS");
        if (property == null) {
            property = System.getProperty("JWT_KEYSTORE_KEYPASS");
        }
        return property;
    }

    public static String jwtKeystoreStorepass() {
        String property = System.getenv("JWT_KEYSTORE_STOREPASS");
        if (property == null) {
            property = System.getProperty("JWT_KEYSTORE_STOREPASS");
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
