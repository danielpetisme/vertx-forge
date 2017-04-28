package io.vertx.forge.generator;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.forge.generator.service.ArchiveService;
import io.vertx.forge.generator.service.ForgeService;
import io.vertx.forge.generator.service.ProjectGeneratorService;
import io.vertx.forge.generator.service.TemplateService;

public class GeneratorVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(GeneratorVerticle.class);

    public static final String TEMPLATE_DIR = "/templates";

    String tempDir() {
        return config().getString("temp.dir", System.getProperty("java.io.tmpdir"));
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        TemplateLoader loader = new ClassPathTemplateLoader(TEMPLATE_DIR);
        ForgeService forge = new ForgeService(vertx, tempDir());
        ProjectGeneratorService generator = new ProjectGeneratorService(vertx, new TemplateService(loader));
        ArchiveService archive = new ArchiveService(vertx);

        vertx.eventBus().<JsonObject>consumer("forge.forge").handler(forge::forge);
        vertx.eventBus().<JsonObject>consumer("forge.clean").handler(forge::clean);
        vertx.eventBus().<JsonObject>consumer("generate").handler(generator::generate);
        vertx.eventBus().<JsonObject>consumer("archive").handler(archive::archive);

        log.info("\n----------------------------------------------------------\n\t" +
                "{} is running!\n" +
                "----------------------------------------------------------",
            GeneratorVerticle.class.getSimpleName());
        startFuture.complete();
    }
}
