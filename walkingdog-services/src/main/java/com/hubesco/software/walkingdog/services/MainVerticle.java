package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.authentication.services.AuthenticationRestVerticle;
import com.hubesco.software.walkingdog.authentication.services.JWTVerticle;
import com.hubesco.software.walkingdog.authentication.services.UsersDbVerticle;
import com.hubesco.software.walkingdog.email.services.EmailVerticle;
import com.hubesco.software.walkingdog.services.location.LocationDbVerticle;
import com.hubesco.software.walkingdog.services.location.LocationRestVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

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
        vertx.deployVerticle(JWTVerticle.class.getName(), (result) -> {
            System.out.println("TokenVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(EmailVerticle.class.getName(), (result) -> {
            System.out.println("EmailVerticle deployment : " + result.succeeded());
        });
        DeploymentOptions optionsUsersDbVerticle = new DeploymentOptions();
        optionsUsersDbVerticle.setInstances(2);
        vertx.deployVerticle(UsersDbVerticle.class.getName(), optionsUsersDbVerticle, (result) -> {
            System.out.println("UsersDbVerticle deployment : " + result.succeeded());
        });
    }

}
