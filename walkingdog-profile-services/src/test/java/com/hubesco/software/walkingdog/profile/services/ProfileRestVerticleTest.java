package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.rest.EndpointHealth;
import com.hubesco.software.walkingdog.commons.rest.EndpointStatus;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author paoesco
 */
@RunWith(VertxUnitRunner.class)
public class ProfileRestVerticleTest extends AbstractVerticleTest {

    @Before
    public void setUp(TestContext context) {
        vertx.deployVerticle(ProfileRestVerticle.class.getName(),
                context.asyncAssertSuccess());
        vertx.deployVerticle(ProfileRestVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
    }

    @Test
    public void testHealthNotAuthorized(TestContext context) {
        final Async async = context.async();
        // WHEN
        vertx.createHttpClient()
                .get(httpPort, "localhost", "/api/profile/health",
                        response -> {
                            context.assertTrue(401 == response.statusCode());
                            async.complete();
                        }).end();
    }

    @Test
    public void testHealth(TestContext context) {
        final Async async = context.async();

        // WHEN
        vertx.createHttpClient()
                .get(httpPort, "localhost", "/api/profile/health",
                        response -> {
                            response.handler(body -> {
                                EndpointHealth health = Json.decodeValue(body.toString(), EndpointHealth.class);
                                context.assertTrue(EndpointStatus.OK.equals(health.getStatus()));
                                async.complete();
                            });
                        }).putHeader("Authorization", "Bearer " + jwtToken).end();
    }

}
