package io.vertx.forge.web.service;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.util.Objects.requireNonNull;

public class DependencyService {

    private static final Logger log = LoggerFactory.getLogger(DependencyService.class);

    private JsonArray dependencies;

    public DependencyService(String dependenciesPath) {
        requireNonNull(dependenciesPath);
        try {
            String raw = new String(
                Files.readAllBytes(new File(dependenciesPath).toPath())
            );
            dependencies = new JsonObject(raw).getJsonArray("content");
        } catch (IOException e) {
            log.error("Impossible to load dependencies at path {}: {}", dependenciesPath, e.getMessage());
        }
    }

    public void findAll(Message<JsonObject> message) {
        JsonObject query = message.body();
        if (dependencies != null) {
            message.reply(dependencies);
        } else {
            message.fail(500, "Impossible to retrieve dependencies");
        }
    }
}
