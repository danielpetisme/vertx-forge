package io.vertx.forge.generator.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ForgeService {

    private final Logger log = LoggerFactory.getLogger(ForgeService.class);

    private Vertx vertx;
    private String tempDir;

    public ForgeService(Vertx vertx, String tempDir) {
        this.vertx = vertx;
        this.tempDir = tempDir;
    }

    JsonObject buildMetadata(JsonObject projectRequest) {
        Path rootDir = Paths.get(tempDir, "vertx-forge", UUID.randomUUID().toString());
        Path baseDir = rootDir.resolve(projectRequest.getString("artifactId", "project"));
        projectRequest.put("rootDir", rootDir.toString());
        projectRequest.put("baseDir", baseDir.toString());
        return projectRequest;
    }

    public void forge(Message<JsonObject> request) {
        JsonObject metadata = buildMetadata(request.body());
        log.info("Forging project with request: {}", metadata);
        createTempDir(metadata)
            .compose(v -> generate(metadata))
            .compose(v -> archive(metadata))
            .setHandler(ar -> {
                if (ar.failed()) {
                    log.error("Impossible to create project {}: {}", metadata, ar.cause().getMessage());
                    request.fail(-1, "Impossible to createProject");
                } else {
                    String archivePath = ar.result();
                    log.debug("Archive forged: {}", archivePath);
                    metadata.put("archivePath", archivePath);
                    vertx.eventBus().publish("forge:created", metadata);
                    request.reply(metadata);
                }
            });
    }

    private Future<Void> generate(JsonObject metadata) {
        Future future = Future.future();
        vertx.eventBus().send("generate", metadata, ar -> {
            if (ar.failed()) {
                log.error(ar.cause().getMessage());
            } else {
                future.complete();
            }
        });
        return future;
    }

    private Future<String> archive(JsonObject metadata) {
        Future future = Future.future();
        vertx.eventBus().send("archive", metadata, ar -> {
            if (ar.failed()) {
                log.error(ar.cause().getMessage());
            } else {
                future.complete(ar.result().body());
            }
        });
        return future;
    }

    private Future<Void> createTempDir(JsonObject metadata) {
        Future future = Future.future();
        String dir = metadata.getString("baseDir");
        vertx.fileSystem().mkdirs(dir, ar -> {
            if (ar.failed()) {
                log.error("Impossible to create temp directory {}: {}", dir, ar.cause().getMessage());
                future.fail(ar.cause());
            } else {
                future.complete();
            }
        });
        return future;
    }


    public void clean(Message<JsonObject> message) {
        JsonObject metadata = message.body();
        String rootDir = metadata.getString("rootDir");
        vertx.fileSystem().deleteRecursive(rootDir, true, ar -> {
            if (ar.failed()) {
                log.error("Impossible to delete temp directory {}: {}", rootDir, ar.cause().getMessage());
            } else {
                log.debug("Temp directory {} deleted", rootDir);
            }
        });

    }
}
