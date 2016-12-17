package com.hubesco.software.walkingdog.services.authentication;

import com.hubesco.software.walkingdog.services.commons.EnvironmentProperties;
import com.hubesco.software.walkingdog.services.commons.eventbus.Addresses;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
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
public class TokenVerticle extends AbstractVerticle {

    private JWTAuth provider;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        try {
            JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                    .put("path", EnvironmentProperties.jwtKeystorePath())
                    .put("type", "jceks")
                    .put("password", EnvironmentProperties.jwtKeystorePassword()));
            provider = JWTAuth.create(vertx, config);
            vertx.eventBus().consumer(Addresses.TOKEN.address(), this::handler);
            startFuture.complete();
        } catch (Exception ex) {
            Logger.getLogger(TokenVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            startFuture.fail(ex);
        }

    }

    private void handler(Message<JsonObject> handler) {
        try {
            switch (handler.headers().get(Headers.COMMAND.header())) {
                case "generate":
                    generate(handler);
                    break;
                case "authenticate":
                    authenticate(handler);
                    break;
                default:
            }
        } catch (Exception ex) {
            Logger.getLogger(TokenVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            handler.fail(500, ex.getLocalizedMessage());
        }
    }

    private void generate(Message<JsonObject> handler) {
        String token = provider.generateToken(new JsonObject().put("email", handler.body().getString("email")), new JWTOptions().setAlgorithm("HS512"));
        handler.reply(token);
    }

    private void authenticate(Message<JsonObject> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
