package com.hubesco.software.walkingdog.services.authentication;

import com.hubesco.software.walkingdog.api.authentication.LoginData;
import com.hubesco.software.walkingdog.api.authentication.SignupData;
import com.hubesco.software.walkingdog.api.commons.DogBreed;
import com.hubesco.software.walkingdog.api.commons.DogGender;
import com.hubesco.software.walkingdog.services.AbstractVerticleTest;
import com.hubesco.software.walkingdog.services.commons.EndpointHealth;
import com.hubesco.software.walkingdog.services.commons.EndpointStatus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
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

    @Before
    public void setUp(TestContext context) {
        vertx.deployVerticle(AuthenticationRestVerticle.class.getName(), context.asyncAssertSuccess());
        vertx.deployVerticle(UsersDbVerticle.class.getName(), context.asyncAssertSuccess());
        vertx.deployVerticle(JWTVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
    }

    @Test
    @Ignore("Route removed becaused of spam calls")
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
                }).close();
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
                    response.bodyHandler(bodyHandler -> {
                        context.assertNotNull(bodyHandler);
                        async.complete();
                    });
                }).end(jsonData);
    }

    @Test
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
        String signupJsonData = Json.encodePrettily(data);
        String signupUrl = "/api/authentication/signup";

        LoginData loginData = new LoginData();
        loginData.setEmail("testLoginUserExistsNotEnabled@walkingdog.com");
        loginData.setPassword("testLoginUserExistsNotEnabled");
        String jsonLoginData = Json.encodePrettily(loginData);
        String loginUrl = "/api/authentication/login";

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
                            }).end(jsonLoginData);
                }).end(signupJsonData);

    }

    @Test
    public void testLoginUserExistsEnabledWrongPassword(TestContext context) {
        final Async async = context.async();

        // GIVEN
        SignupData data = new SignupData();
        data.setEmail("testLoginUserExistsEnabledWrongPassword@walkingdog.com");
        data.setPassword("testLoginUserExistsEnabledWrongPassword");
        data.setDogName("Dog 1");
        data.setDogGender(DogGender.FEMALE);
        data.setDogBreed(DogBreed.SHIBA_INU);
        data.setDogBirthdate("2015-01-01");
        String jsonSignupData = Json.encodePrettily(data);
        String signupUrl = "/api/authentication/signup";

        LoginData loginData = new LoginData();
        loginData.setEmail("testLoginUserExistsEnabledWrongPassword@walkingdog.com");
        loginData.setPassword("XXXXXX");
        String jsonLoginData = Json.encodePrettily(loginData);
        String loginUrl = "/api/authentication/login";

        // WHEN
        vertx.createHttpClient().post(httpPort, "localhost", signupUrl,
                response -> {
                    context.assertTrue(response.statusCode() == 201);
                    response.bodyHandler(bodyHandler -> {
                        JsonObject token = new JsonObject(bodyHandler.toString());
                        String activateUrl = "/api/authentication/activate?token=" + token.getString("token");
                        vertx.createHttpClient().get(httpPort, "localhost", activateUrl,
                                activateResponse -> {
                                    context.assertTrue(activateResponse.statusCode() == 200);
                                    context.assertTrue("OK".equals(activateResponse.statusMessage()));
                                    vertx.createHttpClient().post(httpPort, "localhost", loginUrl,
                                            response2 -> {
                                                // THEN
                                                context.assertTrue(response2.statusCode() == 400);
                                                context.assertTrue("wrong_password".equals(response2.statusMessage()));
                                                async.complete();
                                            }).end(jsonLoginData);
                                }).end();
                    });
                }).end(jsonSignupData);

    }

    @Test
    public void testLoginOK(TestContext context) {
        final Async async = context.async();

        // GIVEN
        SignupData data = new SignupData();
        data.setEmail("testLoginOK@walkingdog.com");
        data.setPassword("testLoginOK");
        data.setDogName("Dog 1");
        data.setDogGender(DogGender.FEMALE);
        data.setDogBreed(DogBreed.SHIBA_INU);
        data.setDogBirthdate("2015-01-01");
        String jsonSignupData = Json.encodePrettily(data);
        String signupUrl = "/api/authentication/signup";

        LoginData loginData = new LoginData();
        loginData.setEmail("testLoginOK@walkingdog.com");
        loginData.setPassword("testLoginOK");
        String jsonLoginData = Json.encodePrettily(loginData);
        String loginUrl = "/api/authentication/login";

        // WHEN
        vertx.createHttpClient().post(httpPort, "localhost", signupUrl,
                response -> {
                    context.assertTrue(response.statusCode() == 201);
                    response.bodyHandler(bodyHandler -> {
                        JsonObject token = new JsonObject(bodyHandler.toString());
                        String activateUrl = "/api/authentication/activate?token=" + token.getString("token");
                        vertx.createHttpClient().get(httpPort, "localhost", activateUrl,
                                activateResponse -> {
                                    context.assertTrue(activateResponse.statusCode() == 200);
                                    context.assertTrue("OK".equals(activateResponse.statusMessage()));
                                    vertx.createHttpClient().post(httpPort, "localhost", loginUrl,
                                            response2 -> {
                                                // THEN
                                                context.assertTrue(response2.statusCode() == 200);
                                                context.assertTrue("OK".equals(response2.statusMessage()));
                                                response2.bodyHandler(loginHandler -> {
                                                    JsonObject loggedUser = loginHandler.toJsonObject();
                                                    context.assertNotNull(loggedUser.getString("uuid"));
                                                    context.assertTrue(data.getEmail().equals(loggedUser.getString("email")));
                                                    context.assertNull(loggedUser.getString("password"));
                                                    context.assertTrue(loggedUser.getBoolean("enabled"));
                                                    context.assertNotNull(loggedUser.getString("token"));
                                                    context.assertNotNull(loggedUser.getString("dogUuid"));
                                                    context.assertTrue(data.getDogName().equals(loggedUser.getString("dogName")));
                                                    context.assertTrue(data.getDogGender().equals(DogGender.valueOf(loggedUser.getString("dogGender"))));
                                                    context.assertTrue(data.getDogBreed().equals(DogBreed.valueOf(loggedUser.getString("dogBreed"))));
                                                    context.assertTrue(data.getDogBirthdate().equals(loggedUser.getString("dogBirthdate")));
                                                    async.complete();
                                                });
                                            }).end(jsonLoginData);
                                }).end();
                    });
                }).end(jsonSignupData);

    }

    @Test
    public void testActivateUserDoesNotExist(TestContext context) {
        final Async async = context.async();

        String activateUrl = "/api/authentication/activate?token=testActivateDoesNotExist";

        // WHEN
        vertx.createHttpClient().get(httpPort, "localhost", activateUrl,
                response -> {
                    context.assertTrue(response.statusCode() == 404);
                    context.assertTrue("user_does_not_exist".equals(response.statusMessage()));
                    async.complete();
                }).end();

    }

    @Test
    public void testActivateUserExists(TestContext context) {
        final Async async = context.async();

        // GIVEN
        SignupData data = new SignupData();
        data.setEmail("testActivateUserExists@walkingdog.com");
        data.setPassword("testActivateUserExists");
        data.setDogName("Dog 1");
        data.setDogGender(DogGender.FEMALE);
        data.setDogBreed(DogBreed.SHIBA_INU);
        data.setDogBirthdate("2015-01-01");
        String signupJsonData = Json.encodePrettily(data);
        String signupUrl = "/api/authentication/signup";

        // WHEN
        vertx.createHttpClient().post(httpPort, "localhost", signupUrl,
                response -> {
                    context.assertTrue(response.statusCode() == 201);
                    response.bodyHandler(bodyHandler -> {
                        JsonObject token = new JsonObject(bodyHandler.toString());
                        String activateUrl = "/api/authentication/activate?token=" + token.getString("token");
                        vertx.createHttpClient().get(httpPort, "localhost", activateUrl,
                                activateResponse -> {
                                    context.assertTrue(activateResponse.statusCode() == 200);
                                    context.assertTrue("OK".equals(activateResponse.statusMessage()));
                                    async.complete();
                                }).end();
                    });
                }).end(signupJsonData);

    }
}
