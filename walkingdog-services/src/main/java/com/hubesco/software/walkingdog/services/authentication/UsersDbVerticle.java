package com.hubesco.software.walkingdog.services.authentication;

import com.hubesco.software.walkingdog.services.commons.eventbus.Addresses;
import com.hubesco.software.walkingdog.services.commons.eventbus.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author paoesco
 */
public class UsersDbVerticle extends AbstractVerticle {

    private AsyncSQLClient postgreSQLClient;

    @Override
    public void start(Future<Void> fut) {
        vertx.eventBus().consumer(Addresses.USER_DB.address(), this::handler);
        try {
            postgreSQLClient = PostgreSQLClient.createShared(vertx, getPostgreSQLClientConfig());
        } catch (Exception ex) {
            Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        fut.complete();
    }

    @Override
    public void stop() throws Exception {
        postgreSQLClient.close();
    }

    private void handler(Message<JsonObject> handler) {
        try {
            switch (handler.headers().get(Headers.COMMAND.header())) {
                case "signup":
                    signup(handler);
                    break;
                case "login":
                    login(handler);
                    break;
                default:
            }
        } catch (Exception ex) {
            Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            handler.fail(500, ex.getLocalizedMessage());
        }
    }

    private void signup(Message<JsonObject> handler) {
        postgreSQLClient.getConnection(connectionHandler -> {
            if (connectionHandler.succeeded()) {
                SQLConnection connection = connectionHandler.result();
                JsonObject newUser = handler.body();
                userExists(connection, newUser)
                        .compose(userExists -> {
                            if (userExists) {
                                handler.fail(400, "User exists");
                                return Future.failedFuture("User exists");
                            } else {
                                return insert(connection, newUser);
                            }
                        })
                        .setHandler(insertResult -> {
                            if (insertResult.succeeded()) {
                                newUser.put("password", "");
                                handler.reply(newUser);
                            } else {
                                handler.fail(500, "Internal Error : " + insertResult.cause().getLocalizedMessage());
                            }
                        });
            } else {
                Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, "Cannot connect to database !!!", connectionHandler.cause());
                handler.fail(500, "Cannot connect to database !!!");
            }
        });
    }

    private void login(Message<JsonObject> handler) {
        postgreSQLClient.getConnection(connectionHandler -> {
            if (connectionHandler.succeeded()) {
                SQLConnection connection = connectionHandler.result();
                JsonObject user = handler.body();
                getUser(connection, user.getString("email"))
                        .setHandler(result -> {
                            if (result.succeeded()) {
                                JsonArray resultUser = result.result();
                                if (!resultUser.getBoolean(2)) {
                                    handler.fail(400, "user_not_enabled");
                                    return;
                                }
                                String encryptedPassword = resultUser.getString(1);
                                if (checkPassword(user.getString("password"), encryptedPassword)) {
                                    handler.reply(resultUser.getString(3));
                                } else {
                                    handler.fail(400, "wrong_password");
                                }
                            } else {
                                handler.fail(404, "user_does_not_exist");
                            }
                        });
            } else {
                Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, "Cannot connect to database !!!", connectionHandler.cause());
                handler.fail(500, "Cannot connect to database !!!");
            }
        });
    }

    private Future<Boolean> userExists(SQLConnection connection, JsonObject user) {
        Future<Boolean> promise = Future.future();
        String email = user.getString("email");
        JsonArray params = new JsonArray();
        params.add(email);
        connection.queryWithParams("SELECT EMAIL FROM T_USER WHERE EMAIL = ?", params, result -> {
            if (result.succeeded()) {
                promise.complete(result.result().getNumRows() == 1);
            } else {
                Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, "SELECT EMAIL FROM T_USER WHERE EMAIL = ?", result.cause());
                promise.fail("Cannot execute query");
            }
        });
        return promise;
    }

    private Future<Boolean> insert(SQLConnection connection, JsonObject user) {
        Future<Boolean> promise = Future.future();
        JsonArray insertUserParams = new JsonArray();
        String userUuid = generateUUID();
        insertUserParams.add(userUuid);
        insertUserParams.add(user.getString("email"));
        insertUserParams.add(encrypt(user.getString("password")));
        insertUserParams.add(generateUUID());

        JsonArray insertDogParams = new JsonArray();
        insertDogParams.add(generateUUID());
        insertDogParams.add(user.getString("dogName"));
        insertDogParams.add(user.getString("dogGender"));
        insertDogParams.add(user.getString("dogBreed"));
        insertDogParams.add(user.getString("dogBirthdate"));
        insertDogParams.add(userUuid);
        connection.updateWithParams("INSERT INTO T_USER (UUID,EMAIL,PASSWORD,TOKEN) values (?,?,?,?)", insertUserParams, result -> {
            if (result.succeeded()) {
                connection.updateWithParams("INSERT INTO T_DOG (UUID,NAME,GENDER,BREED,BIRTHDATE,USER_UUID) values (?,?,?,?,?,?)", insertDogParams, result2 -> {
                    if (result2.succeeded()) {
                        promise.complete(Boolean.TRUE);
                    } else {
                        Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, "INSERT INTO T_DOG (UUID,NAME,GENDER,BREED,BIRTHDATE,USER_UUID) values (?,?,?,?,?,?)", result.cause());
                        promise.fail("Cannot execute query !");
                    }
                });
            } else {
                Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, "Cannot execute query INSERT INTO T_USER (UUID,EMAIL,PASSWORD) values (?,?,?)", result.cause());
                promise.fail("Cannot execute query !");
            }
        });
        return promise;
    }

    private Future<JsonArray> getUser(SQLConnection connection, String email) {
        Future<JsonArray> promise = Future.future();
        JsonArray params = new JsonArray();
        params.add(email);
        connection.queryWithParams("SELECT EMAIL,PASSWORD,ENABLED,TOKEN FROM T_USER WHERE EMAIL = ?", params, result -> {
            if (result.succeeded()) {
                ResultSet resultSet = result.result();
                if (resultSet.getResults().isEmpty()) {
                    promise.fail("User not found");
                } else {
                    promise.complete(resultSet.getResults().get(0));
                }
            } else {
                Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, "SELECT EMAIL FROM T_USER WHERE EMAIL = ?", result.cause());
                promise.fail("Cannot execute query");
            }
        });
        return promise;
    }

    private String encrypt(String password) {
        // Hash a password for the first time
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private JsonObject getPostgreSQLClientConfig() {
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
            Logger.getLogger(UsersDbVerticle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return config;

    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private boolean checkPassword(String actualPassword, String encryptedPassword) {
        return BCrypt.checkpw(actualPassword, encryptedPassword);
    }

}
