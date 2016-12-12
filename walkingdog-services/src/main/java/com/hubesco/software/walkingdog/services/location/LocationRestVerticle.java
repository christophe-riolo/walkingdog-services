package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.api.location.DogLocation;
import com.hubesco.software.walkingdog.services.RouterSingleton;
import com.hubesco.software.walkingdog.services.commons.EndpointHealth;
import com.hubesco.software.walkingdog.services.commons.EndpointStatus;
import com.hubesco.software.walkingdog.services.commons.eventbus.Addresses;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all REST call to Location API.
 *
 * @author paoesco
 */
public class LocationRestVerticle extends AbstractVerticle {

    private static final String API_PREFIX = "/api/location";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = RouterSingleton.router(vertx);

        router.get(API_PREFIX + "/health").handler(this::health);
        router.get(API_PREFIX + "/dogsAround").handler(this::dogsAround);
        router.post(API_PREFIX + "/register").handler(this::register);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        Integer.parseInt(System.getProperty("http.port")),
                        System.getProperty("http.address", "0.0.0.0"),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    /**
     * Endpoint : /api/location/health
     *
     * @param routingContext
     */
    private void health(RoutingContext routingContext) {
        routingContext
                .response()
                .putHeader("content-type", CONTENT_TYPE)
                .end(Json.encodePrettily(new EndpointHealth(EndpointStatus.OK)));
    }

    /**
     * Endpoint : /api/location/dogs
     *
     * @param routingContext
     */
    private void dogsAround(RoutingContext routingContext) {
        // Creates the representation of the displayed map.
        Map map = createMap(routingContext);

        // Gets all locations, filters the locations using the map, sends response.
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader(Headers.COMMAND.header(), "dogs");
        vertx
                .eventBus()
                .send(Addresses.LOCATION_DB.address(), null, options, ebHandler -> {
                    // All locations
                    String jsonAllDogs = (String) ebHandler.result().body();
                    List<String> allDogs = new JsonArray(jsonAllDogs).getList();

                    // Filters them to find dogs located inside the bounds of the map.
                    List<DogLocation> dogsAround = allDogs.stream()
                            .map(json -> {
                                return Json.decodeValue(json, DogLocation.class);
                            })
                            .filter(dogLocation -> {
                                return map.contains(new Point2D.Double(dogLocation.getLongitude(), dogLocation.getLatitude()));
                            })
                            .collect(Collectors.toList());

                    // Sends response
                    routingContext
                            .response()
                            .putHeader("content-type", CONTENT_TYPE)
                            .end(Json.encodePrettily(dogsAround));
                });
    }

    /**
     * Endpoint : /api/location/register
     *
     * @param routingContext
     */
    private void register(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader(Headers.COMMAND.header(), "register");
        vertx.eventBus().send(Addresses.LOCATION_DB.address(), routingContext.getBodyAsString(), options);
        routingContext
                .response()
                .setStatusCode(204)
                .putHeader("content-type", CONTENT_TYPE)
                .end();
    }

    private Map createMap(RoutingContext routingContext) {
        double topLeftY = Double.valueOf(routingContext.request().getParam("ne-lat"));
        double topLeftX = Double.valueOf(routingContext.request().getParam("sw-lon"));
        Point2D topLeft = new Point2D.Double(topLeftX, topLeftY);
        double topRightY = Double.valueOf(routingContext.request().getParam("ne-lat"));
        double topRightX = Double.valueOf(routingContext.request().getParam("ne-lon"));
        Point2D topRight = new Point2D.Double(topRightX, topRightY);
        double bottomRightY = Double.valueOf(routingContext.request().getParam("sw-lat"));
        double bottomRightX = Double.valueOf(routingContext.request().getParam("ne-lon"));
        Point2D bottomRight = new Point2D.Double(bottomRightX, bottomRightY);
        double bottomLeftY = Double.valueOf(routingContext.request().getParam("sw-lat"));
        double bottomLeftX = Double.valueOf(routingContext.request().getParam("sw-lon"));
        Point2D bottomLeft = new Point2D.Double(bottomLeftX, bottomLeftY);
        return new Map(topLeft, topRight, bottomRight, bottomLeft);
    }

}
