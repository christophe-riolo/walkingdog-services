package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.location.api.DogLocation;
import com.hubesco.software.walkingdog.location.api.EventBusEndpoint;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.LocalMap;

/**
 * Handles all call to Location DB.
 *
 * @author paoesco
 */
public class LocationDbVerticle extends AbstractVerticle {

    // List of all dog locations
    private LocalMap<String, String> dogLocations;

    @Override
    public void start(Future<Void> fut) {
        dogLocations = vertx.sharedData().getLocalMap("dogLocations");
        vertx.eventBus().consumer(EventBusEndpoint.LOCATION_DB.address(), this::handler);
        fut.complete();
    }

    private void handler(Message<String> handler) {
        switch (handler.headers().get(Headers.COMMAND.header())) {
            case "register":
                register(handler);
                break;
            case "dogs":
                dogs(handler);
                break;
            default:
        }
    }

    private void register(Message<String> handler) {
        DogLocation dogLocation = Json.decodeValue(handler.body(), DogLocation.class);
        dogLocations.put(dogLocation.getUserUuid(), handler.body());
    }

    private void dogs(Message<String> handler) {
        handler.reply(Json.encodePrettily(dogLocations.values()));
    }

}
