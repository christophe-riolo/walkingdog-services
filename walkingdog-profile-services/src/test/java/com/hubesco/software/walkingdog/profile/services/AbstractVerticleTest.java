package com.hubesco.software.walkingdog.profile.services;

import com.hubesco.software.walkingdog.commons.authentication.KeystoreConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
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
    private static PostgresProcess process;
    protected static AsyncSQLClient postgreSQLClient;

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
        int postgresPort = randomPort();
        System.setProperty("DATABASE_URL", "postgres://postgres:mysecretpassword@localhost:" + postgresPort + "/postgres");
        try {
            // starting Postgres
            final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
            final PostgresConfig config
                    = new PostgresConfig(
                            Version.V9_6_2,
                            new AbstractPostgresConfig.Net("localhost", postgresPort),
                            new AbstractPostgresConfig.Storage("postgres"),
                            new AbstractPostgresConfig.Timeout(),
                            new AbstractPostgresConfig.Credentials("postgres", "mysecretpassword"));
            PostgresExecutable exec = runtime.prepare(config);
            process = exec.start();
            String filePath = Thread.currentThread().getContextClassLoader().getResource("1.0.0.sql").getFile();
            process.importFromFile(new File(filePath));
            postgreSQLClient = PostgreSQLClient.createShared(vertx, getPostgreSQLClientConfig());
        } catch (IOException ex) {
            Logger.getLogger(AbstractVerticleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static JsonObject getPostgreSQLClientConfig() {
        JsonObject config = new JsonObject();
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getProperty("DATABASE_URL");
        }
        try {
            URI dbUri = new URI(databaseUrl);
            String username = dbUri.getUserInfo().split(":")[0];
            config.put("username", username);
            String password = dbUri.getUserInfo().split(":")[1];
            config.put("password", password);
            config.put("host", dbUri.getHost());
            config.put("port", dbUri.getPort());
            config.put("database", dbUri.getPath().replaceAll("/", ""));
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProfileRepositoryVerticle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return config;

    }

    protected static void playSql(String fileName) {
        String filePath = Thread.currentThread().getContextClassLoader().getResource(fileName).getFile();
        process.importFromFile(new File(filePath));
    }

    private static void configureSendGrid() {
        System.setProperty("SENDGRID_API_KEY", "xxx");
    }

}
