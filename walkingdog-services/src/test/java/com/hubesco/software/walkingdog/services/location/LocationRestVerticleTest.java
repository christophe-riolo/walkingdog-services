package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.location.api.DogLocation;
import com.hubesco.software.walkingdog.commons.rest.EndpointHealth;
import com.hubesco.software.walkingdog.commons.rest.EndpointStatus;
import com.hubesco.software.walkingdog.services.AbstractVerticleTest;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author paoesco
 */
@RunWith(VertxUnitRunner.class)
public class LocationRestVerticleTest extends AbstractVerticleTest {

    @Before
    public void setUp(TestContext context) {
        vertx.deployVerticle(LocationRestVerticle.class.getName(),
                context.asyncAssertSuccess());
        vertx.deployVerticle(LocationDbVerticle.class.getName(),
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
                .get(httpPort, "localhost", "/api/location/health",
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
                .get(httpPort, "localhost", "/api/location/health",
                        response -> {
                            response.handler(body -> {
                                EndpointHealth health = Json.decodeValue(body.toString(), EndpointHealth.class);
                                context.assertTrue(EndpointStatus.OK.equals(health.getStatus()));
                                async.complete();
                            });
                        }).putHeader("Authorization", "Bearer " + jwtToken).end();
    }

    @Test
    public void testDogsAround(TestContext context) {
        final Async async = context.async();

        // GIVEN
        String params = paramsDogsAround();
        addDogsAroundData();

        // WHEN
        String url = "/api/location/dogsAround?" + params;
        vertx.createHttpClient().get(httpPort, "localhost", url,
                response -> {
                    response.bodyHandler(body -> {
                        List<DogLocation> dogs = body.toJsonArray().getList();
                        context.assertTrue(Objects.nonNull(dogs));
                        context.assertTrue(dogs.size() == 10);
                        async.complete();
                    });
                }).putHeader("Authorization", "Bearer " + jwtToken).end();
    }

    @Test
    public void testRegister(TestContext context) {
        final Async async = context.async();

        // GIVEN
        DogLocation userLocation = new DogLocation("azertyuiop", "My dog", 0.0, 0.0, true, new Date().getTime());
        String jsonUserLocation = Json.encodePrettily(userLocation);

        // WHEN
        String url = "/api/location/register";
        vertx.createHttpClient().post(httpPort, "localhost", url,
                response -> {
                    context.assertTrue(response.statusCode() == 204);
                    async.complete();
                }).putHeader("Authorization", "Bearer " + jwtToken).end(jsonUserLocation);
    }

    private String paramsDogsAround() {
        StringBuilder params = new StringBuilder();
        params.append("&ne-lat=51.603176");
        params.append("&ne-lon=-0.187197");
        params.append("&sw-lat=51.599313");
        params.append("&sw-lon=-0.199326");
        return params.toString();
    }

    private void addDogsAroundData() {
        LocalMap dogLocations = vertx.sharedData().getLocalMap("dogLocations");
        for (int i = 1; i <= 10; i++) {
            dogLocations.put(i, Json.encode(getRandomDogLocation(i)));
        }

    }

    private static DogLocation getRandomDogLocation(int index) {
        Map map = createMap();
        Point2D randomPoint = getRandomPointInsideMap(map);
        DogLocation dogLocation = new DogLocation("dog" + index, "Dog " + index, randomPoint.getY(), randomPoint.getX(), true, new Date().getTime());
        return dogLocation;
    }

    private static Map createMap() {
        Point2D topLeft = new Point2D.Double(-0.199326, 51.603176);
        Point2D topRight = new Point2D.Double(-0.187197, 51.603176);
        Point2D bottomRight = new Point2D.Double(-0.187197, 51.599313);
        Point2D bottomLeft = new Point2D.Double(-0.199326, 51.599313);
        return new Map(topLeft, topRight, bottomRight, bottomLeft);
    }

    private static Point2D getRandomPointInsideMap(Map map) {
        double x1 = map.getTopLeft().getX();
        double x2 = map.getTopRight().getX();

        double x = ThreadLocalRandom.current().nextDouble(x1 < x2 ? x1 : x2, x1 > x2 ? x1 : x2);

        double y1 = map.getTopLeft().getY();
        double y2 = map.getBottomLeft().getY();
        double y = ThreadLocalRandom.current().nextDouble(y1 < y2 ? y1 : y2, y1 > y2 ? y1 : y2);

        return new Point2D.Double(x, y);
    }

}
