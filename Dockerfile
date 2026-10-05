FROM maven:3.9.16-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

FROM tomcat:9-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /app/target/PetFeet.war /usr/local/tomcat/webapps/ROOT.war
COPY docker/start.sh /usr/local/bin/petfeet-start.sh
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "/usr/local/bin/petfeet-start.sh"]
