package io.vertx.forge.web.rest;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.forge.web.util.RestUtil.error;
import static io.vertx.forge.web.util.RestUtil.respondJson;

public class DependencyResource {

    private static final Logger log = LoggerFactory.getLogger(DependencyResource.class);

    private EventBus eventBus;

    public DependencyResource(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void findAll(RoutingContext rc) {
        log.debug("REST request to get all Versions");
        this.eventBus.send("dependency.query", new JsonObject(), ar -> {
            if (ar.succeeded()) {
                respondJson(rc, (JsonArray) ar.result().body());
            } else {
                error(rc, ar.cause());
            }
        });
    }
}
