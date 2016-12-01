package com.hubesco.software.walkingdog.services.map;

import com.hubesco.software.walkingdog.services.AbstractVerticleTest;
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
public class MapVerticleTest extends AbstractVerticleTest {

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
        vertx.createHttpClient().getNow(port, "localhost", "/api/map/health",
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
        String url = "/api/map/dogsAround?" + paramsDogsAround();
        vertx.createHttpClient().getNow(port, "localhost", url,
                response -> {
                    response.handler(body -> {
                        List<DogLocation> dogs = body.toJsonArray().getList();
                        context.assertTrue(Objects.nonNull(dogs));
                        context.assertTrue(dogs.size() == 5);
                        async.complete();
                    });
                });
    }

    private String paramsDogsAround() {
        StringBuilder params = new StringBuilder();
        params.append("&ne-lat=51.603176");
        params.append("&ne-lon=-0.187197");
        params.append("&sw-lat=51.599313");
        params.append("&sw-lon=-0.199326");
        return params.toString();
    }

}
