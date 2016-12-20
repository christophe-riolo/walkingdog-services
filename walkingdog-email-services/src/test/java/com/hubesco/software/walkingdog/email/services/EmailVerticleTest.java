package com.hubesco.software.walkingdog.email.services;

import com.hubesco.software.walkingdog.commons.eventbus.Addresses;
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
 *
 * @author paoesco
 */
@RunWith(VertxUnitRunner.class)
public class EmailVerticleTest extends AbstractVerticleTest {

    @Before
    public void setUp(TestContext context) {
        vertx.deployVerticle(EmailVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
    }

    @Test
    @Ignore(value = "Ignored because didn't find a way to mock sendgrid API key")
    public void testSendActivationEmail(TestContext context) {
        final Async async = context.async();

        JsonObject email = new JsonObject()
                .put("from", "contact@walkingdogapp.com")
                .put("to", "pao.esco@outlook.com")
                .put("subject", "Test subject")
                .put("content", "Test content");
        vertx.eventBus().send(Addresses.EMAIL_SERVICES.address(), email, handler -> {
            if (handler.succeeded()) {
                context.assertTrue(true);
            } else {
                context.fail("An error occured");
            }
            async.complete();
        });

    }

}
