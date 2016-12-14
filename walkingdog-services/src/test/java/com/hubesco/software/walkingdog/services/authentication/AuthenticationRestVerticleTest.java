package com.hubesco.software.walkingdog.services.authentication;

import com.hubesco.software.walkingdog.api.authentication.LoginData;
import com.hubesco.software.walkingdog.api.authentication.SignupData;
import com.hubesco.software.walkingdog.api.commons.DogBreed;
import com.hubesco.software.walkingdog.api.commons.DogGender;
import com.hubesco.software.walkingdog.services.AbstractVerticleTest;
import com.hubesco.software.walkingdog.services.commons.EndpointHealth;
import com.hubesco.software.walkingdog.services.commons.EndpointStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author paoesco
 */
@RunWith(VertxUnitRunner.class)
public class AuthenticationRestVerticleTest extends AbstractVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(AuthenticationRestVerticle.class.getName(),
                context.asyncAssertSuccess());
        vertx.deployVerticle(UsersDbVerticle.class.getName(),
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
        vertx.createHttpClient().getNow(httpPort, "localhost", "/api/authentication/health",
                response -> {
                    response.handler(body -> {
                        EndpointHealth health = Json.decodeValue(body.toString(), EndpointHealth.class);
                        context.assertTrue(EndpointStatus.OK.equals(health.getStatus()));
                        async.complete();
                    });
                });
    }

    @Test
    public void testSignupUserDoesNotExist(TestContext context) {
        final Async async = context.async();

        // GIVEN
        SignupData data = new SignupData();
        data.setEmail("testSignupUserDoesNotExist@walkingdog.com");
        data.setPassword("testSignupUserDoesNotExist");
        data.setDogName("Dog 1");
        data.setDogGender(DogGender.FEMALE);
        data.setDogBreed(DogBreed.SHIBA_INU);
        data.setDogBirthdate("2015-01-01");
        String jsonData = Json.encodePrettily(data);

        // WHEN
        String url = "/api/authentication/signup";
        vertx.createHttpClient().post(httpPort, "localhost", url,
                response -> {
                    // THEN
                    context.assertTrue(response.statusCode() == 201);
                    async.complete();
                }).end(jsonData);
    }

    @Test
    @Ignore
    public void testSignupUserExists(TestContext context) {
        final Async async = context.async();

        // GIVEN
        SignupData data = new SignupData();
        data.setEmail("testSignupUserExists@walkingdog.com");
        data.setPassword("testSignupUserExists");
        data.setDogName("Dog 1");
        data.setDogGender(DogGender.FEMALE);
        data.setDogBreed(DogBreed.SHIBA_INU);
        data.setDogBirthdate("2015-01-01");
        String jsonData = Json.encodePrettily(data);
        String url = "/api/authentication/signup";

        // WHEN
        vertx.createHttpClient().post(httpPort, "localhost", url,
                response -> {
                    context.assertTrue(response.statusCode() == 201);
                    vertx.createHttpClient().post(httpPort, "localhost", url,
                            response2 -> {
                                // THEN
                                context.assertTrue(response2.statusCode() == 400);
                                async.complete();
                            }).end(jsonData);
                }).end(jsonData);

    }

    @Test
    @Ignore
    public void testLoginUserDoesNotExist(TestContext context) {
        final Async async = context.async();

        // GIVEN
        LoginData data = new LoginData();
        data.setEmail("testLoginUserDoesNotExist@walkingdog.com");
        data.setPassword("testLoginUserDoesNotExist");
        String jsonData = Json.encodePrettily(data);

        // WHEN
        String url = "/api/authentication/login";
        vertx.createHttpClient().post(httpPort, "localhost", url,
                response -> {
                    // THEN
                    context.assertTrue(response.statusCode() == 404);
                    context.assertTrue("user_does_not_exist".equals(response.statusMessage()));
                    async.complete();
                }).end(jsonData);
    }

    @Test
    public void testLoginUserExistsNotEnabled(TestContext context) {
        final Async async = context.async();

        // GIVEN
        SignupData data = new SignupData();
        data.setEmail("testLoginUserExistsNotEnabled@walkingdog.com");
        data.setPassword("testLoginUserExistsNotEnabled");
        data.setDogName("Dog 1");
        data.setDogGender(DogGender.FEMALE);
        data.setDogBreed(DogBreed.SHIBA_INU);
        data.setDogBirthdate("2015-01-01");
        String jsonData = Json.encodePrettily(data);
        String signupUrl = "/api/authentication/signup";
        
        LoginData loginData = new LoginData();
        loginData.setEmail("testLoginUserExistsNotEnabled@walkingdog.com");
        loginData.setPassword("testLoginUserExistsNotEnabled");
        String loginUrl = "/api/authentication/signup";

        // WHEN
        vertx.createHttpClient().post(httpPort, "localhost", signupUrl,
                response -> {
                    context.assertTrue(response.statusCode() == 201);
                    vertx.createHttpClient().post(httpPort, "localhost", loginUrl,
                            response2 -> {
                                // THEN
                                context.assertTrue(response2.statusCode() == 400);
                                context.assertTrue("user_not_enabled".equals(response2.statusMessage()));
                                async.complete();
                            }).end(jsonData);
                }).end(jsonData);

    }
    
//    @Test
//    public void testLoginUserExistsEnabledWrongPassword(TestContext context) {
//        final Async async = context.async();
//
//        // GIVEN
//        SignupData data = new SignupData();
//        data.setEmail("testLoginUserExistsEnabledWrongPassword@walkingdog.com");
//        data.setPassword("testLoginUserExistsEnabledWrongPassword");
//        data.setDogName("Dog 1");
//        data.setDogGender(DogGender.FEMALE);
//        data.setDogBreed(DogBreed.SHIBA_INU);
//        data.setDogBirthdate("2015-01-01");
//        String jsonData = Json.encodePrettily(data);
//        String signupUrl = "/api/authentication/signup";
//        
//        LoginData loginData = new LoginData();
//        loginData.setEmail("testLoginUserExistsEnabledWrongPassword@walkingdog.com");
//        loginData.setPassword("testLoginUserExistsEnabledWrongPassword");
//        String loginUrl = "/api/authentication/signup";
//
//        // WHEN
//        vertx.createHttpClient().post(httpPort, "localhost", signupUrl,
//                response -> {
//                    context.assertTrue(response.statusCode() == 201);
//                    vertx.createHttpClient().post(httpPort, "localhost", loginUrl,
//                            response2 -> {
//                                // THEN
//                                context.assertTrue(response2.statusCode() == 400);
//                                context.assertTrue("user_not_enabled".equals(response2.statusMessage()));
//                                async.complete();
//                            }).end(jsonData);
//                }).end(jsonData);
//
//    }
}
