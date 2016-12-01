package com.hubesco.software.walkingdog.services.map;

import com.hubesco.software.walkingdog.services.common.EndpointHealth;
import com.hubesco.software.walkingdog.services.common.EndpointStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author paoesco
 */
public class MapVerticle extends AbstractVerticle {

    private static final String API_PREFIX = "/api/map";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = Router.router(vertx);

        CorsHandler cors = CorsHandler.create("*").allowedMethod(HttpMethod.GET);
        router.route().handler(cors);
        router.get(API_PREFIX + "/health").handler(this::health);
        router.get(API_PREFIX + "/dogsAround").handler(this::dogsAround);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        Integer.parseInt(System.getProperty("http.port")),
                        System.getProperty("http.address", "0.0.0.0"),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private void health(RoutingContext routingContext) {
        routingContext
                .response()
                .putHeader("content-type", CONTENT_TYPE)
                .end(Json.encodePrettily(new EndpointHealth(EndpointStatus.OK)));
    }

    private void dogsAround(RoutingContext routingContext) {
        // Creates the representation of the displayed map.
        Map displayedMap = createMap(routingContext);
        // Find dogs located inside the map
        List<DogLocation> dogsAround = findDogs(displayedMap);
        // Send response
        routingContext
                .response()
                .putHeader("content-type", CONTENT_TYPE)
                .end(Json.encodePrettily(dogsAround));
    }

    private Map createMap(RoutingContext routingContext) {
        double topLeftY = Double.valueOf(routingContext.request().getParam("ne-lat"));
        double topLeftX = Double.valueOf(routingContext.request().getParam("sw-lon"));
        Point2D topLeft = new Point2D.Double(topLeftX, topLeftY);
        double topRightY = Double.valueOf(routingContext.request().getParam("ne-lat"));
        double topRightX = Double.valueOf(routingContext.request().getParam("ne-lon"));
        Point2D topRight = new Point2D.Double(topRightX, topRightY);
        double bottomRightY = Double.valueOf(routingContext.request().getParam("sw-lat"));
        double bottomRightX = Double.valueOf(routingContext.request().getParam("ne-lon"));
        Point2D bottomRight = new Point2D.Double(bottomRightX, bottomRightY);
        double bottomLeftY = Double.valueOf(routingContext.request().getParam("sw-lat"));
        double bottomLeftX = Double.valueOf(routingContext.request().getParam("sw-lon"));
        Point2D bottomLeft = new Point2D.Double(bottomLeftX, bottomLeftY);
        return new Map(topLeft, topRight, bottomRight, bottomLeft);
    }

    /**
     * Find dogs located inside the boundaries of the map.
     *
     * @param map : boundaries
     * @return list if DogLocation
     */
    private List<DogLocation> findDogs(Map map) {
        // Get all locations
        List<DogLocation> allDogs = getAllDogs(map);
        // Filter them to find dogs located inside the bounds of the map.
        List<DogLocation> dogsAround = allDogs.stream()
                .filter(dogLocation -> {
                    return map.contains(new Point2D.Double(dogLocation.getLongitude(), dogLocation.getLatitude()));
                })
                .collect(Collectors.toList());
        return dogsAround;
    }

    /**
     * To replace with real implementation.
     *
     * @param map
     * @return
     */
    private List<DogLocation> getAllDogs(Map map) {
        List<DogLocation> allDogs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Point2D randomPoint = getRandomPointInsideMap(map);
            DogLocation dogLocation = new DogLocation("dog" + i, "Dog " + i, randomPoint.getY(), randomPoint.getX());
            allDogs.add(dogLocation);
        }
        return allDogs;
    }

    private Point2D getRandomPointInsideMap(Map map) {
        double x1 = map.getTopLeft().getX();
        double x2 = map.getTopRight().getX();

        double x = ThreadLocalRandom.current().nextDouble(x1 < x2 ? x1 : x2, x1 > x2 ? x1 : x2);

        double y1 = map.getTopLeft().getY();
        double y2 = map.getBottomLeft().getY();
        double y = ThreadLocalRandom.current().nextDouble(y1 < y2 ? y1 : y2, y1 > y2 ? y1 : y2);

        return new Point2D.Double(x, y);
    }

}
