package io.vertx.forge.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public final class RestUtil {

    public static void error(RoutingContext rc, Throwable cause) {
        rc.response().setStatusCode(HTTP_INTERNAL_ERROR).end(cause.getMessage());
    }

    private static void respond(RoutingContext rc, String contentType, String chunk) {
        rc
            .response()
            .setStatusCode(HTTP_OK)
            .putHeader("Content-Type", contentType)
            .end(chunk);
    }

    public static void respondJson(RoutingContext rc, JsonObject object) {
        respond(rc, "application/json", object.encode());
    }

    public static void respondJson(RoutingContext rc, JsonArray array) {
        respond(rc, "application/json", array.encode());
    }
}
