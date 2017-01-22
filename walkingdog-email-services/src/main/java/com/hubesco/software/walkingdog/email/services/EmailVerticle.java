package com.hubesco.software.walkingdog.email.services;

import com.hubesco.software.walkingdog.commons.EnvironmentProperties;
import com.hubesco.software.walkingdog.email.api.EventBusEndpoint;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paoesco
 */
public class EmailVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        try {
            vertx.eventBus().consumer(EventBusEndpoint.EMAIL_SERVICES.address(), this::send);
            startFuture.complete();
        } catch (Exception ex) {
            Logger.getLogger(EmailVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            startFuture.fail(ex);
        }
    }

    private void send(Message<JsonObject> handler) {
        //Email from = new Email("contact@walkingdogapp.com");
        Email from = new Email(handler.body().getString("from"));
        //String subject = "WalkingDog - Account activation";
        String subject = handler.body().getString("subject");
        Email to = new Email(handler.body().getString("to"));
        //Content content = new Content("text/plain", "Hello ! In order to activate your account, please click on the following URL : " + handler.body().getString("activationUrl"));
        Content content = new Content("text/plain", handler.body().getString("content"));
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(EnvironmentProperties.sendGridApiKey());
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            Object[] params = {from.getEmail(), response.statusCode};
            List<Integer> successCodes = new ArrayList<>();
            successCodes.add(200);
            successCodes.add(202);
            if (successCodes.contains(response.statusCode)) {
                Logger.getLogger(EmailVerticle.class.getName()).log(Level.INFO, "Email successfully sent to {0} with status {1}", params);
                handler.reply(response.statusCode);
            } else {
                Logger.getLogger(EmailVerticle.class.getName()).log(Level.INFO, "Email UNSUCESSFULLY sent to {0} with status {1}", params);
                handler.fail(response.statusCode, response.body);
            }
        } catch (IOException ex) {
            Logger.getLogger(EmailVerticle.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            handler.fail(500, ex.getLocalizedMessage());
        }
    }

}
