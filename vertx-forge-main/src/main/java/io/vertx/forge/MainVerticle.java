package io.vertx.forge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.forge.generator.GeneratorVerticle;
import io.vertx.forge.web.WebVerticle;

import static io.vertx.core.Future.future;
import static java.util.Arrays.asList;

public class MainVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future<String> generatorFuture = future();
        vertx.deployVerticle(
            GeneratorVerticle.class.getName(),
            new DeploymentOptions().setConfig(config().getJsonObject("generator")),
            generatorFuture
        );
        Future<String> webFuture = future();
        vertx.deployVerticle(
            WebVerticle.class.getName(),
            new DeploymentOptions().setConfig(config().getJsonObject("web")),
            webFuture
        );

        CompositeFuture.all(asList(generatorFuture, webFuture)).setHandler(ar -> {
            if (ar.failed()) {
                log.error("Vertx forge failed to start: {}", ar.cause().getMessage());
                ar.cause().printStackTrace();
            } else {
                log.info("\n----------------------------------------------------------\n\t" +
                        "{} is running!\n" +
                        "----------------------------------------------------------",
                    MainVerticle.class.getSimpleName());
                startFuture.complete();
            }
        });
    }
}
