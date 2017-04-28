#!/usr/bin/env bash
mvn clean package; \
java \
-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory \
-jar \
vertx-forge-main/target/vertx-forge-main-*-fat.jar \
-conf \
vertx-forge-main/conf/default-conf.json


