###
# vert.x docker example using a Java verticle packaged as a fatjar
# To build:
#  docker build -t sample/vertx-java-fat .
# To run:
#   docker run -t -i -p 8080:8080 sample/vertx-java-fat
###

FROM java:openjdk-8-jdk

ENV CONF_FILE vertx-forge-main/conf/default-conf.json
ENV VERTICLE_FILE vertx-forge-main/target/vertx-forge-main-1.0.0-SNAPSHOT-fat.jar

COPY . .
RUN ./mvnw clean package
EXPOSE 8080

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory $VERTICLE_FILE -conf $CONF_FILE"]
