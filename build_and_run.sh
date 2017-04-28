#!/usr/bin/env bash

java_opts="-Dvertx.logger-delegate-factory-class-name=\"io.vertx.core.logging.SLF4JLogDelegateFactory\""
jar_name="vertx-forge-main/target/vertx-forge-main-1.0.0-SNAPSHOT-fat.jar"
vertx_opts="-conf vertx-forge-main/conf/default-conf.json"

mvn clean package && \
mvn exec:exec -Dexec.executable="java" -Dexec.args="${java_opts} -jar ${jar_name} ${vertx_opts}"
