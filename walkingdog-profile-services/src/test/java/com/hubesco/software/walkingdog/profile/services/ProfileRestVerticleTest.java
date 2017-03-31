package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.model.DogBreed;
import com.hubesco.software.walkingdog.commons.model.DogGender;
import com.hubesco.software.walkingdog.commons.rest.EndpointHealth;
import com.hubesco.software.walkingdog.commons.rest.EndpointStatus;
import com.hubesco.software.walkingdog.email.services.EmailVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author paoesco
 */
@RunWith(VertxUnitRunner.class)
public class ProfileRestVerticleTest extends AbstractVerticleTest {

    @BeforeClass
    public static void beforeClass(TestContext context) {
        vertx.deployVerticle(ProfileRestVerticle.class.getName(), context.asyncAssertSuccess());
        vertx.deployVerticle(ProfileRepositoryVerticle.class.getName(), context.asyncAssertSuccess());
        vertx.deployVerticle(EmailVerticle.class.getName(), context.asyncAssertSuccess());
        playSql("ProfileRestVerticleTest.sql");
    }

    @Test
    public void testHealthNotAuthorized(TestContext context) {
        final Async async = context.async();
        // WHEN
        vertx.createHttpClient()
                .get(httpPort, "localhost", "/api/profile/health",
                        response -> {
                            context.assertEquals(401, response.statusCode());
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
                            response.bodyHandler(body -> {
                                EndpointHealth health = Json.decodeValue(body.toString(), EndpointHealth.class);
                                context.assertTrue(EndpointStatus.OK.equals(health.getStatus()));
                                async.complete();
                            });
                        }).putHeader("Authorization", "Bearer " + jwtToken).end();
    }

    @Test
    @Ignore
    public void testUpdateNotAuthorized(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient()
                .post(httpPort, "localhost", "/api/profile/" + "notauthorized", response -> {
                    response.bodyHandler(bodyHandler -> {
                        context.assertEquals(401, response.statusCode());
                        async.complete();
                    });
                });
    }

    @Test
    public void testUpdateUserOk(TestContext context) {
        final Async async = context.async();

        // GIVEN
        // User in base
        // Request
        JsonObject profile = new JsonObject();
        String uuid = "userwithdog";
        String dogUuid = "mydogtochange";
        profile.put("uuid", uuid);
        profile.put("email", "walkingdog@yopmail.com");
        profile.put("dogUuid", dogUuid);
        profile.put("dogGender", DogGender.MALE);
        profile.put("dogBreed", DogBreed.AMERICAN_WATER_SPANIEL);
        profile.put("dogName", "NewName");
        profile.put("dogBirthdate", "2000-01-01");
        profile.put("dogBase64Image", "http://image.jpeg");

        // WHEN
        vertx.createHttpClient()
                .post(httpPort, "localhost", "/api/profile/" + uuid, response -> {
                    response.bodyHandler(bodyHandler -> {
                        // THEN
                        context.assertEquals(200, response.statusCode());
                        checkUpdate(context, async, dogUuid);
                    });
                })
                .putHeader("Authorization", "Bearer " + jwtToken)
                .end(profile.encode());

    }

    @Test
    public void testGetDogImage(TestContext context) {
        // GIVEN
        // User in base

        // WHEN
        vertx.createHttpClient()
                .get(httpPort, "localhost", "/api/profile/getImageUserUuid/dogs/getImageDogUuid/image", response -> {
                    response.bodyHandler(bodyHandler -> {
                        // THEN
                        context.assertEquals(200, response.statusCode());
                        context.assertEquals("http://getimage.png", bodyHandler.toString());
                    });
                })
                .putHeader("Authorization", "Bearer " + jwtToken);

    }

    private void checkUpdate(TestContext context, Async async, String dogUuid) {
        postgreSQLClient.getConnection(connectionHandler -> {
            SQLConnection connection = connectionHandler.result();
            String selectQuery = "SELECT d.UUID, d.NAME, d.BASE64IMAGE, d.GENDER, d.BREED, d.BIRTHDATE, d.USER_UUID from T_DOG d where d.UUID=? ";
            connection.queryWithParams(selectQuery, new JsonArray().add(dogUuid), resultHandler -> {
                JsonArray dog = resultHandler.result().getResults().get(0);
                context.assertEquals("mydogtochange", dog.getString(0));
                context.assertEquals("NewName", dog.getString(1));
                context.assertEquals("http://image.jpeg", dog.getString(2));
                context.assertEquals(DogGender.MALE.toString(), dog.getString(3));
                context.assertEquals(DogBreed.AMERICAN_WATER_SPANIEL.toString(), dog.getString(4));
                context.assertEquals("2000-01-01", dog.getString(5));
                connection.close(handler -> {
                    async.complete();
                });
            });
        });
    }

}
