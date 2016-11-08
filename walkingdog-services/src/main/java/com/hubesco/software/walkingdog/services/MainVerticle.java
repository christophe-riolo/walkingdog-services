package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.services.map.MapVerticle;
import io.vertx.core.AbstractVerticle;

/**
 * @author paoesco
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(MapVerticle.class.getName(), (result) -> {
            System.out.println("MapVerticle deployment : " + result.succeeded());
        });
    }

}
