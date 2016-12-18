package com.hubesco.software.walkingdog.commons.authentication;

import com.hubesco.software.walkingdog.commons.EnvironmentProperties;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author paoes
 */
public final class KeystoreConfig {

    private KeystoreConfig() {
    }

    public static JsonObject config() {
        return new JsonObject().put("keyStore", new JsonObject()
                .put("path", EnvironmentProperties.jwtKeystorePath())
                .put("type", "jceks")
                .put("password", EnvironmentProperties.jwtKeystorePassword()));
    }

}
