FROM openjdk:8-alpine
ADD target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","jar","emedlogixserv.jar"]