package com.hubesco.software.walkingdog.services.authentication;

import com.hubesco.software.walkingdog.services.commons.eventbus.Addresses;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paoesco
 */
public class UsersDbVerticleMock extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx.eventBus().consumer(Addresses.USER_DB.address(), this::handler);
        fut.complete();
    }

    private void handler(Message<JsonObject> handler) {
        switch (handler.headers().get(Headers.COMMAND.header())) {
            case "signup":
                signup(handler);
                break;
            default:
        }
    }
    
    private void signup(Message<JsonObject> handler) {
        if ("userexists@walkingdog.com".equals(handler.body().getString("email"))) {
            handler.fail(400, "User exists");
        } else {
            handler.reply(handler.body());
        }
    }

}
