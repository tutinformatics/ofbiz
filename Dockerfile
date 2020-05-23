FROM ubuntu:18.04

MAINTAINER tutinformatics

# copy files to workdir
ADD . /ofbiz
WORKDIR /ofbiz

# Install OpenJDK-11
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME
ENV JAVA_OPTS="-Dfile.encoding=UTF-8"

# Setup backend connection
RUN cat deploy/entity/entityengine.xml > framework/entity/config/entityengine.xml

# Fix line endings
RUN apt-get install dos2unix
RUN dos2unix ./gradlew

EXPOSE 8443
EXPOSE 8080
EXPOSE 4567
EXPOSE 1099

# Run ofbiz
ENTRYPOINT ./gradlew cleanAll loadAll ofbiz

