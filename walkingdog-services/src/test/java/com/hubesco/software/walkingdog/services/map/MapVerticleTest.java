package com.hubesco.software.walkingdog.services.map;

import com.hubesco.software.walkingdog.services.common.EndpointHealth;
import com.hubesco.software.walkingdog.services.common.EndpointStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.util.List;
import java.util.Objects;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author paoesco
 */
@RunWith(VertxUnitRunner.class)
public class MapVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(MapVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
        final Async async = context.async();
        vertx.createHttpClient().getNow(8080, "localhost", "/api/map/health",
                response -> {
                    response.handler(body -> {
                        EndpointHealth health = Json.decodeValue(body.toString(), EndpointHealth.class);
                        context.assertTrue(EndpointStatus.OK.equals(health.getStatus()));
                        async.complete();
                    });
                });
    }

    @Test
    public void testDogsAround(TestContext context) {
        final Async async = context.async();
        vertx.createHttpClient().getNow(8080, "localhost", "/api/map/dogsAround",
                response -> {
                    response.handler(body -> {
                        List<DogLocation> dogs = body.toJsonArray().getList();
                        context.assertTrue(Objects.nonNull(dogs));
                        context.assertTrue(dogs.size() == 2);
                        async.complete();
                    });
                });
    }

}
