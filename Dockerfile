FROM openjdk:1.8
EXPOSE 8080
ADD target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","-jar","/emedlogixserv.jar"]