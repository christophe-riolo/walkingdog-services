package com.hubesco.software.walkingdog.authentication.services;

import io.vertx.core.Vertx;
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
    protected static int postgresPort;
    private static PostgresProcess process;
    protected static Vertx vertx;

    public AbstractVerticleTest() {
    }

    @BeforeClass
    public static void beforeClass() {
        vertx = Vertx.vertx();
        httpPort = randomPort();
        System.setProperty("http.port", String.valueOf(httpPort));
        configureJwt();
        configureEmbeddedPostgres();
        configureSendGrid();
//        configureLocalPostgres();
    }

    @AfterClass
    public static void afterClass() {
        if (process != null) {
            process.stop();
        }
        vertx.close();
    }

    private static void configureLocalPostgres() {
        System.setProperty("DATABASE_URL", "postgres://postgres:postgres@localhost:5432/postgres");
    }

    private static void configureJwt() {
        System.setProperty("JWT_KEYSTORE_PASSWORD", "secretpassword");
        System.setProperty("JWT_KEYSTORE_PATH", "keystore_jwt-test.jceks");
    }

    private static void configureSendGrid() {
        System.setProperty("SENDGRID_API_KEY", "xxx");
    }

    private static void configureEmbeddedPostgres() {
        postgresPort = randomPort();
        System.setProperty("DATABASE_URL", "postgres://postgres:mysecretpassword@localhost:" + postgresPort + "/postgres");
        try {
            // starting Postgres
            final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
            final PostgresConfig config
                    = new PostgresConfig(
                            Version.V9_5_0,
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
