package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.commons.authentication.KeystoreConfig;
import com.hubesco.software.walkingdog.location.api.DogLocation;
import com.hubesco.software.walkingdog.services.location.Map;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author pescobar
 */
public class Runner {

    public static void main(String[] args) throws IOException {

        int defaultPort = 8080;
        System.setProperty("http.port", String.valueOf(defaultPort));
        System.setProperty("DATABASE_URL", "postgres://postgres:mysecretpassword@localhost:5432/walkingdog");
        System.setProperty("JWT_KEYSTORE_PASSWORD", "secretpassword");
        System.setProperty("JWT_KEYSTORE_PATH", "keystore_jwt-test.jceks");
        System.setProperty("SENDGRID_API_KEY", "xxx");
        JWTAuth provider = JWTAuth.create(Vertx.vertx(), KeystoreConfig.config());
        String jwtToken = provider.generateToken(new JsonObject().put("email", "auth@walkingdog.com"), new JWTOptions().setAlgorithm("HS512"));

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), (result) -> {
            System.out.println("MainVerticle deployment : " + result.succeeded());

            // Adds some dummy data for testing purpose.
            for (int i = 1; i <= 10; i++) {
                vertx
                        .createHttpClient()
                        .post(defaultPort, "localhost", "/api/location/register")
                        .handler(null)
                        .putHeader("Authorization", "Bearer " + jwtToken)
                        .end(Json.encode(getRandomDogLocation(i)));

            }

        });

    }

    private static DogLocation getRandomDogLocation(int index) {
        Map map = createMap();
        Point2D randomPoint = getRandomPointInsideMap(map);
        DogLocation dogLocation = new DogLocation(UUID.randomUUID().toString(), "dog" + index, "Dog " + index, randomPoint.getY(), randomPoint.getX(), true, new Date().getTime());
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
