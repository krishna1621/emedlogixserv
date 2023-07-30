FROM openjdk:8-alpine
ADD target/emedlogixservice.jar emedlogixservice.jar
ENTRYPOINT ["java","-jar","/emedlogixservice.jar"]