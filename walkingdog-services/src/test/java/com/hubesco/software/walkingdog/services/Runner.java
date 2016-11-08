package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.services.map.MapVerticle;
import io.vertx.core.Vertx;

/**
 *
 * @author pescobar
 */
public class Runner {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(MapVerticle.class.getName(), (result) -> {
            System.out.println("MapVerticle deployment : " + result.succeeded());
        });
    }

}
