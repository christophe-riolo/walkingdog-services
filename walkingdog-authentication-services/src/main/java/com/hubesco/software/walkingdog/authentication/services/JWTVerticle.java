package com.hubesco.software.walkingdog.authentication.services;

import com.hubesco.software.walkingdog.authentication.api.EventBusEndpoint;
import com.hubesco.software.walkingdog.commons.authentication.KeystoreConfig;
import com.hubesco.software.walkingdog.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author paoesco
 */
public class JWTVerticle extends AbstractVerticle {

    private JWTAuth provider;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        try {
            provider = JWTAuth.create(vertx, KeystoreConfig.config());
            vertx.eventBus().consumer(EventBusEndpoint.AUTHENTICATION_JWT.address(), this::handler);
            startFuture.complete();
        } catch (Exception ex) {
            Logger.getLogger(JWTVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            startFuture.fail(ex);
        }

    }

    private void handler(Message<JsonObject> handler) {
        try {
            switch (handler.headers().get(Headers.COMMAND.header())) {
                case "generate":
                    generate(handler);
                    break;
                default:
            }
        } catch (Exception ex) {
            Logger.getLogger(JWTVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            handler.fail(500, ex.getLocalizedMessage());
        }
    }

    private void generate(Message<JsonObject> handler) {
        String token = provider.generateToken(new JsonObject().put("email", handler.body().getString("email")), new JWTOptions().setAlgorithm("HS512"));
        handler.reply(token);
    }
}
