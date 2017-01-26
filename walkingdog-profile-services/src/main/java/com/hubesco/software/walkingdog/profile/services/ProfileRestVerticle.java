package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.rest.EndpointHealth;
import com.hubesco.software.walkingdog.commons.rest.EndpointStatus;
import com.hubesco.software.walkingdog.commons.rest.RouterSingleton;
import com.hubesco.software.walkingdog.profile.api.EventBusEndpoint;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * @author paoesco
 */
public class ProfileRestVerticle extends AbstractVerticle {

    private static final String API_PREFIX = "/api/profile";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = RouterSingleton.router(vertx);

        router.get(API_PREFIX + "/health").handler(this::health);
        router.post(API_PREFIX + "/:uuid").handler(this::update);

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
     * Endpoint : /api/profile/:uuid
     *
     * @param routingContext
     */
    public void update(RoutingContext routingContext) {
        //String uuid = routingContext.request().getParam("uuid");
        vertx.eventBus().send(
                EventBusEndpoint.PROFILE_REPOSITORY.address(),
                routingContext.getBodyAsJson(),
                handler -> {
                    if (handler.succeeded()) {
                        JsonObject user = (JsonObject) handler.result().body();
                        routingContext.response()
                                .setStatusCode(201)
                                .putHeader("content-type", CONTENT_TYPE)
                                .end(user.encode());
                    } else {
                        ReplyException cause = (ReplyException) handler.cause();
                        routingContext.response()
                                .setStatusCode(cause.failureCode())
                                .setStatusMessage(cause.getLocalizedMessage())
                                .putHeader("content-type", CONTENT_TYPE)
                                .end();
                    }
                });
    }

}
