###
# vert.x docker example using a Java verticle packaged as a fatjar
# To build:
#  docker build -t sample/vertx-java-fat .
# To run:
#   docker run -t -i -p 8080:8080 sample/vertx-java-fat
###

FROM java:8-jre

ENV CONF_FILE default-conf.json
ENV VERTICLE_FILE vertx-forge-main-1.0.0-SNAPSHOT-fat.jar
ENV DEPENDENCIES_FILE dependencies.json

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY vertx-forge-main/target/$VERTICLE_FILE $VERTICLE_HOME/
COPY vertx-forge-main/conf/$CONF_FILE $VERTICLE_HOME/
COPY $DEPENDENCIES_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory $VERTICLE_FILE -conf $CONF_FILE"]
