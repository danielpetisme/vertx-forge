package io.vertx.forge.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.forge.web.rest.DependencyResource;
import io.vertx.forge.web.rest.ForgeResource;
import io.vertx.forge.web.rest.VersionResource;
import io.vertx.forge.web.service.DependencyService;
import io.vertx.forge.web.service.VersionService;

public class WebVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(WebVerticle.class);
    public static final int DEFAULT_HTTP_PORT = 8080;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        int port = config().getInteger("http.port", DEFAULT_HTTP_PORT);
        Router router = Router.router(vertx);
        cors(router);
        //TODO data should come from a DB, not a file or hardcoded values
        dependencies(router, config().getString("dependencies.path"));
        versions(router);
        forge(router, config().getJsonObject("project.request"));
        router.route().handler(StaticHandler.create());
        vertx
            .createHttpServer()
            .requestHandler(router::accept)
            .listen(port, ar -> {
                if (ar.failed()) {
                    log.error("Fail to start {}: {}", WebVerticle.class.getSimpleName(), ar.cause().getMessage());
                    startFuture.fail(ar.cause());
                } else {
                    log.info("\n----------------------------------------------------------\n\t" +
                            "{} is running! Access URLs:\n\t" +
                            "Local: \t\thttp://localhost:{}\n" +
                            "----------------------------------------------------------",
                        WebVerticle.class.getSimpleName(), port);
                    startFuture.complete();
                }
            });
    }

    private void cors(Router router) {
        router.route().handler(CorsHandler.create("*")
            .allowedMethod(HttpMethod.GET)
            .allowedMethod(HttpMethod.POST)
            .allowedHeader("Content-Type")
            .allowedHeader("Accept")
        );
    }

    private void dependencies(Router router, String dependenciesPath) {
        DependencyResource dependencyResource = new DependencyResource(vertx.eventBus());
        router.get("/dependencies").handler(dependencyResource::findAll);
        DependencyService dependencyService = new DependencyService(dependenciesPath);
        vertx.eventBus().consumer("dependency.query", dependencyService::findAll);
    }

    private void versions(Router router) {
        VersionResource versionResource = new VersionResource(vertx.eventBus());
        router.get("/versions").handler(versionResource::findAll);
        VersionService versionService = new VersionService();
        vertx.eventBus().consumer("version.query", versionService::findAll);

    }

    private void forge(Router router, JsonObject projectRequest) {
        ForgeResource forgeResource = new ForgeResource(vertx.eventBus(), projectRequest);
        router.get("/starter*").handler(forgeResource::forge);
    }
}
