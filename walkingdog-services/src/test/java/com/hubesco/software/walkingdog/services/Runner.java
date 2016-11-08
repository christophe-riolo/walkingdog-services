package com.hubesco.software.walkingdog.services;

import io.vertx.core.Vertx;
import java.io.IOException;

/**
 *
 * @author pescobar
 */
public class Runner {

    public static void main(String[] args) throws IOException {

        System.setProperty("http.port", "8080");

        Vertx.vertx().deployVerticle(MainVerticle.class.getName(), (result) -> {
            System.out.println("MainVerticle deployment : " + result.succeeded());
        });
    }

}
