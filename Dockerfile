ARG zuluJDKVersion=17-jdk-alpine
FROM docker.ci.artifacts.prod.walmart.com/strati-docker/zulu:${zuluJDKVersion} as build
RUN mkdir -p /opt/app
COPY target/scala-2.12/assembly/*.jar /opt/app/*.jar
COPY version.sbt /opt/app/version.sbt
WORKDIR /opt/app