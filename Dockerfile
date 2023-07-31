FROM openjdk:8-alpine
WORKDIR /emedlogixserv
ADD emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java", "jar", "/emedlogixserv.jar"]