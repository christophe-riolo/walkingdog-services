package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.api.location.DogLocation;
import com.hubesco.software.walkingdog.api.location.UserLocation;
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
public class LocationRestVerticleTest extends AbstractVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(LocationRestVerticle.class.getName(),
                context.asyncAssertSuccess());
        vertx.deployVerticle(LocationDbVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testHealth(TestContext context) {
        final Async async = context.async();

        // WHEN
        vertx.createHttpClient().getNow(port, "localhost", "/api/location/health",
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

        // GIVEN
        String params = paramsDogsAround();

        // WHEN
        String url = "/api/location/dogsAround?" + params;
        vertx.createHttpClient().getNow(port, "localhost", url,
                response -> {
                    response.bodyHandler(body -> {
                        List<DogLocation> dogs = body.toJsonArray().getList();
                        context.assertTrue(Objects.nonNull(dogs));
                        context.assertTrue(dogs.size() == 10);
                        async.complete();
                    });
                });
    }

    @Test
    public void testRegister(TestContext context) {
        final Async async = context.async();

        // GIVEN
        UserLocation userLocation = new UserLocation("azertyuiop", 0.0, 0.0);
        String jsonUserLocation = Json.encodePrettily(userLocation);

        // WHEN
        String url = "/api/location/register";
        vertx.createHttpClient().post(port, "localhost", url,
                response -> {
                    context.assertTrue(response.statusCode() == 200);
                    async.complete();
                }).end(jsonUserLocation);
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
