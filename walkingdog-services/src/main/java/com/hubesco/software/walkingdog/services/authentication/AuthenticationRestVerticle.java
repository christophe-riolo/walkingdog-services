package com.hubesco.software.walkingdog.services.authentication;

import com.hubesco.software.walkingdog.services.RouterSingleton;
import com.hubesco.software.walkingdog.services.commons.EndpointHealth;
import com.hubesco.software.walkingdog.services.commons.EndpointStatus;
import com.hubesco.software.walkingdog.services.commons.eventbus.Addresses;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author paoesco
 */
public class AuthenticationRestVerticle extends AbstractVerticle {

    private static final String API_PREFIX = "/api/authentication";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = RouterSingleton.router(vertx);

        router.get(API_PREFIX + "/health").handler(this::health);
        router.post(API_PREFIX + "/signup").handler(this::signup);

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
     * Endpoint : /api/authentication/health
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
     * Endpoint : /api/authentication/signup
     *
     * @param routingContext
     */
    private void signup(RoutingContext routingContext) {
        JsonObject body = routingContext.getBodyAsJson();
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader(Headers.COMMAND.header(), "signup");
        vertx
                .eventBus()
                .send(Addresses.USER_DB.address(), body, options, handler -> {
                    int statusCode = 201;
                    if (handler.failed()) {
                        ReplyException cause = (ReplyException) handler.cause();
                        statusCode = cause.failureCode();
                    }
                    body.put("password", "");
                    routingContext
                            .response()
                            .setStatusCode(statusCode)
                            .putHeader("content-type", CONTENT_TYPE)
                            .end(body.encode());
                });

    }

}
