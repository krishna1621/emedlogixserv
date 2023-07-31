FROM openjdk:8
ADD target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","-jar","/emedlogixserv.jar"]