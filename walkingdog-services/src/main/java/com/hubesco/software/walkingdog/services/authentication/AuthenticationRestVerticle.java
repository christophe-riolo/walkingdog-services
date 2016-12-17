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
import io.vertx.core.http.HttpServerResponse;
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
        router.post(API_PREFIX + "/login").handler(this::login);
        router.get(API_PREFIX + "/activate").handler(this::activate);

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
                    HttpServerResponse response = routingContext
                            .response()
                            .setStatusCode(201)
                            .putHeader("content-type", CONTENT_TYPE);
                    if (handler.succeeded()) {
                        JsonObject jsonToken = (JsonObject) handler.result().body();
                        response.end(jsonToken.encode());
                    } else {
                        ReplyException cause = (ReplyException) handler.cause();
                        response
                                .setStatusCode(cause.failureCode())
                                .setStatusMessage(cause.getLocalizedMessage())
                                .end();
                    }

                });

    }

    /**
     * Endpoint : /api/authentication/login
     *
     * @param routingContext
     */
    private void login(RoutingContext routingContext) {
        loginBackend(routingContext)
                .compose(loggedUser -> {
                    return generateToken(loggedUser);
                })
                .setHandler(handler -> {
                    if (handler.succeeded()) {
                        routingContext
                                .response()
                                .setStatusCode(200)
                                .setStatusMessage("OK")
                                .putHeader("content-type", CONTENT_TYPE)
                                .end(handler.result().encode());
                    } else {
                        ReplyException cause = (ReplyException) handler.cause();
                        routingContext
                                .response()
                                .setStatusCode(cause.failureCode())
                                .setStatusMessage(cause.getLocalizedMessage())
                                .putHeader("content-type", CONTENT_TYPE)
                                .end();
                    }
                });
    }

    private Future<JsonObject> loginBackend(RoutingContext routingContext) {
        Future<JsonObject> promise = Future.future();
        JsonObject body = routingContext.getBodyAsJson();
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader(Headers.COMMAND.header(), "login");
        vertx.eventBus()
                .send(Addresses.USER_DB.address(), body, options, handler -> {
                    if (handler.succeeded()) {
                        promise.complete((JsonObject) handler.result().body());
                    } else {
                        promise.fail(handler.cause());
                    }

                });
        return promise;
    }

    private Future<JsonObject> generateToken(JsonObject loggedUser) {
        Future<JsonObject> promise = Future.future();
        // Get token
        DeliveryOptions options = new DeliveryOptions().addHeader(Headers.COMMAND.header(), "generate");
        vertx.eventBus()
                .send(Addresses.TOKEN.address(), loggedUser, options, handler -> {
                    if (handler.succeeded()) {
                        loggedUser.put("token", (String) handler.result().body());
                        promise.complete(loggedUser);
                    } else {
                        promise.fail(handler.cause());
                    }

                });
        return promise;
    }

    /**
     * Endpoint : /api/authentication/activate
     *
     * @param routingContext
     */
    private void activate(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put("token", routingContext.request().getParam("token"));
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader(Headers.COMMAND.header(), "activate");
        vertx
                .eventBus()
                .send(Addresses.USER_DB.address(), data, options, handler -> {
                    int statusCode = 200;
                    String responseBody = "<html><body><h1>Account successfully activated !</h1></body></html>";
                    String statusMessage = "OK";
                    if (handler.failed()) {
                        ReplyException cause = (ReplyException) handler.cause();
                        statusCode = cause.failureCode();
                        statusMessage = cause.getLocalizedMessage();
                        responseBody = "<html><body><h1>An error occured during activation...</h1></body></html>";
                    }
                    routingContext
                            .response()
                            .setStatusCode(statusCode)
                            .setStatusMessage(statusMessage)
                            .putHeader("content-type", "text/html; charset=utf-8")
                            .end(responseBody);
                });

    }

}
