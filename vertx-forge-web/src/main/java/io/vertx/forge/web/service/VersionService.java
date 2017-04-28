package io.vertx.forge.web.service;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class VersionService {

    public static final JsonArray VERSIONS = new JsonArray()
        .add("3.4.1")
        .add("3.4.0");

    public void findAll(Message<JsonObject> message) {
        JsonObject query = message.body();
        message.reply(VERSIONS);
    }
}
