package io.vertx.forge.generator.service;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;

public class TemplateService {

    private final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private Handlebars handlebars;

    public TemplateService(TemplateLoader loader) {
        this.handlebars = new Handlebars(loader);
    }

    public Future<String> render(String template, JsonObject object) {
        Context context = Context.newBuilder(object.getMap()).resolver(MapValueResolver.INSTANCE).build();
        log.debug("Rendering template {} with object {}", object);
        Future future = Future.future();
        try {
            future.complete(handlebars.compile(template).apply(context));
        } catch (IOException e) {
            log.error("Impossible to render template {}: ", template, e.getMessage());
            future.fail(e.getCause());
        }
        return future;
    }

}
