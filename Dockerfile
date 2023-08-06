FROM openjdk:8-alpine
WORKDIR/emedlogixserv
COPY target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","jar","emedlogixserv.jar"]