package io.vertx.forge.generator.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

public class ArchiveService {

    private final Logger log = LoggerFactory.getLogger(ArchiveService.class);

    private Vertx vertx;

    public ArchiveService(Vertx vertx) {
        this.vertx = vertx;
    }

    public void archive(Message<JsonObject> message) {
        JsonObject metadata = message.body();
        String baseDir = metadata.getString("baseDir");
        String rootDir = metadata.getString("rootDir");
        String archive = rootDir + "/archive.zip";
        vertx.fileSystem().createFile(archive, ar -> {
            if (ar.failed()) {
                log.error("Impossible to create file {}: {}", archive, ar.cause().getMessage());
                message.fail(500, ar.cause().getMessage());
            } else {
                ZipUtil.pack(new File(baseDir), new File(archive), true);
                message.reply(archive);
            }
        });
    }
}
