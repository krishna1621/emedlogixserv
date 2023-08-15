FROM openjdk:8-alpine
COPY /target emedlogixserv.jar emedlogixserv.jar/
ENTRYPOINT ["java","jar","/emedlogixserv.jar"]