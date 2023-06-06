FROM eclipse-temurin:17 as build

LABEL org.opencontainers.image.authors="rafal.szyszka@bpower2.com"

# Install git
RUN apt-get -y update
RUN apt-get -y install git

# Configure git cert for downloading code
RUN mkdir -p certs/git
COPY certs/git/gitlab.sonelli-group.com.pem certs/git
RUN git config --global http.CAInfo /certs/git/gitlab.sonelli-group.com.pem
RUN git config --global http.sslVerify false

# Configure application files which will lay in /home directory
WORKDIR /home
RUN git clone https://microservices.docker.bot:Z^EMBM9fesHd3CgbQpuR2fvmSLpNYPy3@gitlab.sonelli-group.com:444/bpower3/micro-services/data-service.git

WORKDIR /home/data-service
RUN chmod 755 mvnw
RUN ./mvnw install -DskipTests


FROM tomcat:jdk17-temurin
VOLUME /tmp
ARG DEPENDENCY=/home/data-service/target
COPY --from=build ${DEPENDENCY}/DataService-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/