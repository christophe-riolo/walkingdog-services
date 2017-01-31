package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.eventbus.Headers;
import com.hubesco.software.walkingdog.profile.api.EventBusEndpoint;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paoesco
 */
public class ProfileRepositoryVerticle extends AbstractVerticle {

    private AsyncSQLClient postgreSQLClient;

    @Override
    public void start(Future<Void> fut) {
        vertx.eventBus().consumer(EventBusEndpoint.PROFILE_REPOSITORY.address(), this::handler);
        try {
            postgreSQLClient = PostgreSQLClient.createShared(vertx, getPostgreSQLClientConfig());
        } catch (Exception ex) {
            Logger.getLogger(ProfileRepositoryVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        fut.complete();
    }

    @Override
    public void stop() throws Exception {
        postgreSQLClient.close();
    }

    private void handler(Message<JsonObject> handler) {
        try {
            switch (handler.headers().get(Headers.COMMAND.header())) {
                case "update":
                    update(handler);
                    break;
                default:
            }
        } catch (Exception ex) {
            Logger.getLogger(ProfileRepositoryVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            handler.fail(500, ex.getLocalizedMessage());
        }
    }

    private void update(Message<JsonObject> handler) {
        postgreSQLClient.getConnection(connectionHandler -> {
            if (connectionHandler.succeeded()) {
                SQLConnection connection = connectionHandler.result();
                update(connection, handler.body())
                        .setHandler(updateHandler -> {
                            if (updateHandler.succeeded()) {
                                handler.reply(handler.body());
                            } else {
                                handler.fail(500, updateHandler.cause().getLocalizedMessage());
                            }
                            connection.close();
                        });

            } else {
                Logger.getLogger(ProfileRepositoryVerticle.class.getName()).log(Level.SEVERE, "Cannot connect to database !!!", connectionHandler.cause());
                handler.fail(500, "Cannot connect to database !!!");
            }
        });
    }

    private Future<Void> update(SQLConnection connection, JsonObject profile) {
        Future<Void> promise = Future.future();
        JsonArray params = new JsonArray();
        params.add(profile.getString("dogName"));
        params.add(profile.getString("dogGender"));
        params.add(profile.getString("dogBreed"));
        params.add(profile.getString("dogBirthdate"));
        params.add(profile.getString("dogUuid"));
        params.add(profile.getString("uuid"));
        connection.updateWithParams("UPDATE T_DOG SET NAME=?, GENDER=?, BREED=?, BIRTHDATE=? WHERE UUID=? AND USER_UUID=? ", params, handler -> {
            if (handler.succeeded()) {
                promise.complete();
            } else {
                Logger.getLogger(ProfileRepositoryVerticle.class.getName()).log(Level.SEVERE, "Cannot update dog with uuid=" + profile.getString("dogUuid"), handler.cause());
                promise.fail(handler.cause());
            }
        });
        return promise;
    }

    private JsonObject getPostgreSQLClientConfig() {
        JsonObject config = new JsonObject();
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getProperty("DATABASE_URL");
        }
        try {
            URI dbUri = new URI(databaseUrl);
            String username = dbUri.getUserInfo().split(":")[0];
            config.put("username", username);
            String password = dbUri.getUserInfo().split(":")[1];
            config.put("password", password);
            config.put("host", dbUri.getHost());
            config.put("port", dbUri.getPort());
            config.put("database", dbUri.getPath().replaceAll("/", ""));
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProfileRepositoryVerticle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return config;

    }

}
