
FROM openjdk:8

MAINTAINER tutinformatics

# copy files to workdir
ADD . /ofbiz

WORKDIR /ofbiz

RUN ./gradlew

RUN ./gradlew cleanAll loadAll

# Use volume mount for no restart xml changes etc.
VOLUME /ofbiz

# Run ofbiz
ENTRYPOINT ./gradlew ofbiz