package com.hubesco.software.walkingdog.email.services;

import io.vertx.core.Vertx;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author paoesco
 */
public abstract class AbstractVerticleTest {

    protected static Vertx vertx;

    public AbstractVerticleTest() {
    }

    @BeforeClass
    public static void beforeClass() {
        vertx = Vertx.vertx();
        System.setProperty("SENDGRID_API_KEY", "xxx");
    }

    @AfterClass
    public static void afterClass() {
        vertx.close();
    }

}
