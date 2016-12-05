package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.services.location.LocationDbVerticle;
import com.hubesco.software.walkingdog.services.location.LocationRestVerticle;
import io.vertx.core.AbstractVerticle;

/**
 * @author paoesco
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(LocationRestVerticle.class.getName(), (result) -> {
            System.out.println("LocationRestVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(LocationDbVerticle.class.getName(), (result) -> {
            System.out.println("LocationDbVerticle deployment : " + result.succeeded());
        });
    }

}
