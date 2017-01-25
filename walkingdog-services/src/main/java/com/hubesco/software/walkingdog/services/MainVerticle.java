package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.authentication.services.AuthenticationRestVerticle;
import com.hubesco.software.walkingdog.authentication.services.JWTVerticle;
import com.hubesco.software.walkingdog.authentication.services.AuthenticationRepositoryVerticle;
import com.hubesco.software.walkingdog.email.services.EmailVerticle;
import com.hubesco.software.walkingdog.profile.services.ProfileRepositoryVerticle;
import com.hubesco.software.walkingdog.profile.services.ProfileRestVerticle;
import com.hubesco.software.walkingdog.services.location.LocationDbVerticle;
import com.hubesco.software.walkingdog.services.location.LocationRestVerticle;
import io.vertx.core.AbstractVerticle;

/**
 * @author paoesco
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // Location services
        vertx.deployVerticle(LocationRestVerticle.class.getName(), (result) -> {
            System.out.println("LocationRestVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(LocationDbVerticle.class.getName(), (result) -> {
            System.out.println("LocationDbVerticle deployment : " + result.succeeded());
        });
        // Authentication services
        vertx.deployVerticle(AuthenticationRestVerticle.class.getName(), (result) -> {
            System.out.println("AuthenticationRestVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(AuthenticationRepositoryVerticle.class.getName(), (result) -> {
            System.out.println("AuthenticationRepositoryVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(JWTVerticle.class.getName(), (result) -> {
            System.out.println("JWTVerticle deployment : " + result.succeeded());
        });
        // Email services
        vertx.deployVerticle(EmailVerticle.class.getName(), (result) -> {
            System.out.println("EmailVerticle deployment : " + result.succeeded());
        });
        // Profile services
        vertx.deployVerticle(ProfileRestVerticle.class.getName(), (result) -> {
            System.out.println("ProfileRestVerticle deployment : " + result.succeeded());
        });
        vertx.deployVerticle(ProfileRepositoryVerticle.class.getName(), (result) -> {
            System.out.println("ProfileRepositoryVerticle deployment : " + result.succeeded());
        });
    }

}
