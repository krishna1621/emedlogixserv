FROM openjdk:8-alpine
WORKDIR /emedlogixserv
ADD /absolute/path/to/target/emedlogixserv.jar emedlogixserv.jar
ENTRYPOINT ["java","jar","/emedlogixserv.jar"]