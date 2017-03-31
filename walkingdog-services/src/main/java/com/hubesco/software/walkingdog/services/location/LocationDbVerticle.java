package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.location.api.DogLocation;
import com.hubesco.software.walkingdog.location.api.EventBusEndpoint;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.LocalMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all call to Location DB.
 *
 * @author paoesco
 */
public class LocationDbVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(LocationDbVerticle.class);

    // List of all dog locations
    private LocalMap<String, String> dogLocations;

    @Override
    public void start(Future<Void> fut) {
        dogLocations = vertx.sharedData().getLocalMap("dogLocations");
        dogLocations.clear();
        vertx.eventBus().consumer(EventBusEndpoint.LOCATION_DB.address(), this::handler);
        vertx.setPeriodic(getOneMinute(), this::cleanTimeoutLocations);
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
        dogLocations.put(dogLocation.getDogUuid(), handler.body());
    }

    private void dogs(Message<String> handler) {
        handler.reply(Json.encodePrettily(dogLocations.values()));
    }

    /**
     * Cleans location that are not updated for in the last minute.
     *
     * @param timerId
     */
    private void cleanTimeoutLocations(Long timerId) {
        log.debug("START cleanLocations");
        long currentTimestamp = new Date().getTime();
        // Collects locations to be removed
        List<String> locationsToRemove = new ArrayList<>();
        dogLocations.keySet().stream().filter((dogUuid) -> {
            DogLocation dogLocation = Json.decodeValue(dogLocations.get(dogUuid), DogLocation.class);
            return (currentTimestamp - dogLocation.getLastUpdated()) > getOneMinute();
        }).collect(Collectors.toList());
        // Removes all the location
        locationsToRemove.forEach(dogUuid -> {
            dogLocations.remove(dogUuid);
        });
        log.debug("END cleanLocations");
    }

    private long getOneMinute() {
        return 1000 * 60;
    }

}
