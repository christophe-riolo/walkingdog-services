package com.hubesco.software.walkingdog.services;

import com.hubesco.software.walkingdog.commons.authentication.KeystoreConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 *
 * @author paoesco
 */
public abstract class AbstractVerticleTest {

    protected static int httpPort;
    private static JWTAuth provider;
    protected static String jwtToken;
    protected static Vertx vertx;

    public AbstractVerticleTest() {
    }

    @BeforeClass
    public static void beforeClass() {
        vertx = Vertx.vertx();
        httpPort = randomPort();
        System.setProperty("http.port", String.valueOf(httpPort));
        configureJwt();
    }

    @AfterClass
    public static void afterClass() {
        vertx.close();
    }

    private static void configureJwt() {
        System.setProperty("JWT_KEYSTORE_PASSWORD", "secretpassword");
        System.setProperty("JWT_KEYSTORE_PATH", "keystore_jwt-test.jceks");
        provider = JWTAuth.create(Vertx.vertx(), KeystoreConfig.config());
        jwtToken = provider.generateToken(new JsonObject().put("email", "auth@walkingdog.com"), new JWTOptions().setAlgorithm("HS512"));
    }

    private static int randomPort() {
        int port = -1;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        } catch (IOException ex) {
            Logger.getLogger(AbstractVerticleTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getLocalizedMessage());
        }
        return port;
    }

}
