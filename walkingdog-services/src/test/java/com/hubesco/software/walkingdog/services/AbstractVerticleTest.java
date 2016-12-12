package com.hubesco.software.walkingdog.services;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;

/**
 *
 * @author paoesco
 */
public abstract class AbstractVerticleTest {

    protected int port;

    public AbstractVerticleTest() {
        System.setProperty("DATABASE_URL", "postgres://postgres:mysecretpassword@192.168.99.100:5432/postgres");
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
            System.setProperty("http.port", String.valueOf(port));
        } catch (IOException ex) {
            Logger.getLogger(AbstractVerticleTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getLocalizedMessage());
        }
    }

}
