FROM openjdk:8-alpine
WORKDIR /emedlogixserv
ADD target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","jar","/emedlogixserv.jar"]