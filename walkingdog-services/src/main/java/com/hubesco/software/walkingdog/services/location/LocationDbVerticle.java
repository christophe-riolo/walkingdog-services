package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.api.location.UserLocation;
import com.hubesco.software.walkingdog.services.common.eventbus.Addresses;
import com.hubesco.software.walkingdog.services.common.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles all call to Location DB.
 *
 * @author paoesco
 */
public class LocationDbVerticle extends AbstractVerticle {

    // List of all locations
    private Map<String, UserLocation> usersLocations;

    @Override
    public void start(Future<Void> fut) {
        usersLocations = new HashMap<>();
        vertx.eventBus().consumer(Addresses.LOCATION_DB.address(), this::handler);
        fut.complete();
    }

    private void handler(Message<String> handler) {
        switch (handler.headers().get(Headers.COMMAND.header())) {
            case "register":
                UserLocation userLocation =  Json.decodeValue(handler.body(), UserLocation.class);
                usersLocations.put(userLocation.getId(), userLocation);
            default:
        }
    }

}
