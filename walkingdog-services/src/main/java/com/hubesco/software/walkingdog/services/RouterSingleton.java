package com.hubesco.software.walkingdog.services;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author paoesco
 */
public class RouterSingleton {

    private static Router router;

    public synchronized static Router router(Vertx vertx) {
        if (router == null) {
            Set<HttpMethod> methods = new HashSet<>();
            methods.add(HttpMethod.GET);
            methods.add(HttpMethod.POST);
            router = Router.router(vertx);
            CorsHandler cors = CorsHandler
                    .create("*")
                    .allowedMethods(methods);
            router.route().handler(cors);
            router.route().handler(BodyHandler.create());
        }
        return router;
    }

}
