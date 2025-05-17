# Dockerfile
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/library-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]