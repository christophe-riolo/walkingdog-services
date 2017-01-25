package com.hubesco.software.walkingdog.profile.services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 *
 * @author paoesco
 */
public class ProfileRepositoryVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        fut.complete();
    }

}
