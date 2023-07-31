FROM openjdk:8-alpine
WORKDIR /emedlogixserv
ADD target/emedlogixservice.jar emedlogixservice.jar
ENTRYPOINT ["java","-jar","/emedlogixservice.jar"]