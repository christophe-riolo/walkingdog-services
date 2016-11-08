package com.hubesco.software.walkingdog.services.map;

import com.hubesco.software.walkingdog.services.common.EndpointHealth;
import com.hubesco.software.walkingdog.services.common.EndpointStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author paoesco
 */
public class MapVerticle extends AbstractVerticle {

    private static final String API_PREFIX = "/api/map";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = Router.router(vertx);

        router.get(API_PREFIX + "/health").handler(this::health);
        router.get(API_PREFIX + "/dogsAround").handler(this::dogsAround);

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

    private void health(RoutingContext routingContext) {
        routingContext
                .response()
                .putHeader("content-type", CONTENT_TYPE)
                .end(Json.encodePrettily(new EndpointHealth(EndpointStatus.OK)));
    }

    private void dogsAround(RoutingContext routingContext) {
        // Creates some dogs
        List<DogLocation> dogs = new ArrayList<>();
        DogLocation dog1 = new DogLocation("dog1", "Dog 1", 0, 0);
        dogs.add(dog1);
        DogLocation dog2 = new DogLocation("dog2", "Dog 2", 0, 1);
        dogs.add(dog2);

        routingContext
                .response()
                .putHeader("content-type", CONTENT_TYPE)
                .end(Json.encodePrettily(dogs));
    }

}
