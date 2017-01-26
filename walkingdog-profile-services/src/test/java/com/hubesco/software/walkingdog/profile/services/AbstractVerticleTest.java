package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.authentication.KeystoreConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

/**
 *
 * @author paoesco
 */
public abstract class AbstractVerticleTest {

    protected static int httpPort;
    private static JWTAuth provider;
    protected static String jwtToken;
    protected static Vertx vertx;
    protected static int postgresPort;
    private static PostgresProcess process;

    public AbstractVerticleTest() {
    }

    @BeforeClass
    public static void beforeClass() {
        vertx = Vertx.vertx();
        httpPort = randomPort();
        System.setProperty("http.port", String.valueOf(httpPort));
        configureJwt();
        configureEmbeddedPostgres();
    }

    @AfterClass
    public static void afterClass() {
        if (process != null) {
            process.stop();
        }
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

    private static void configureEmbeddedPostgres() {
        postgresPort = randomPort();
        System.setProperty("DATABASE_URL", "postgres://postgres:mysecretpassword@localhost:" + postgresPort + "/postgres");
        try {
            // starting Postgres
            final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
            final PostgresConfig config
                    = new PostgresConfig(
                            Version.V9_6_0,
                            new AbstractPostgresConfig.Net("localhost", postgresPort),
                            new AbstractPostgresConfig.Storage("postgres"),
                            new AbstractPostgresConfig.Timeout(),
                            new AbstractPostgresConfig.Credentials("postgres", "mysecretpassword"));
            PostgresExecutable exec = runtime.prepare(config);
            process = exec.start();
            String filePath = Thread.currentThread().getContextClassLoader().getResource("1.0.0.sql").getFile();
            process.importFromFile(new File(filePath));
        } catch (IOException ex) {
            Logger.getLogger(AbstractVerticleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
