package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.eventbus.Headers;
import com.hubesco.software.walkingdog.profile.api.EventBusEndpoint;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
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
        throw new UnsupportedOperationException("Not yet implemetend");
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
