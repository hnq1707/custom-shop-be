# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only files needed to resolve dependencies first (better layer caching)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
RUN mvn -q -DskipTests dependency:go-offline

# Now copy the source and build
COPY src src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Default environment variables (can be overridden at runtime)
ENV SERVER_PORT=8080 \
    MONGODB_URI="" \
    DESIGNS_DIR="/app/uploads/designs" \
    JAVA_OPTS=""

# Expose Spring Boot default port
EXPOSE 8080

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Ensure uploads directory exists and can be mounted as a volume
RUN mkdir -p /app/uploads/designs
VOLUME ["/app/uploads"]

# Start the application. Spring will read env vars referenced in application.properties
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
