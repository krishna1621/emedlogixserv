FROM openjdk:8
WORKDIR/emedlogixserv
EXPOSE 8080
COPY target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","-jar","/emedlogixserv.jar"]