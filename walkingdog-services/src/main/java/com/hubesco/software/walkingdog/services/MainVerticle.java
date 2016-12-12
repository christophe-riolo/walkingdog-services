package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.services.authentication.AuthenticationRestVerticle;
import com.hubesco.software.walkingdog.services.authentication.UsersDbVerticle;
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
        vertx.deployVerticle(AuthenticationRestVerticle.class.getName(), (result) -> {
            System.out.println("AuthenticationRestVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(UsersDbVerticle.class.getName(), (result) -> {
            System.out.println("UsersDbVerticle deployment : " + result.succeeded());
        });
    }

}
