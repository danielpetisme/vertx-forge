package io.vertx.forge.web.rest;

import io.vertx.ext.web.Router;

public interface Resource {

    Router registerRoutes(Router router);
}
