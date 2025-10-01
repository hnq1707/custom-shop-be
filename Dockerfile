# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only files needed to resolve dependencies first (better layer caching)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
RUN mvn -q -DskipTests dependency:go-offline

# Copy source code and build
COPY src src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install CA certificates for SSL
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*

# Default environment variables (can be overridden via .env)
ENV SERVER_PORT=8080 \
    SPRING_APPLICATION_NAME=ecommerce_be \
    SPRING_DATA_MONGODB_URI="mongodb+srv://hnq1707:quyen177@cluster0.0phizh4.mongodb.net/mydb?authSource=admin" \
    SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB \
    SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB \
    APP_UPLOADS_DESIGNS_DIR="/app/uploads/designs" \
    APP_AI_OPENAI_API_KEY="" \
    APP_AI_HF_API_KEY="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" \
    APP_AI_PROVIDER="stub" \
    APP_AI_HF_TEXT2IMG_MODEL="stabilityai/stable-diffusion-xl-base-1.0" \
    APP_CORS_ALLOWED_ORIGINS="*" \
    LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG \
    LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG \
    JAVA_OPTS="-Dhttps.protocols=TLSv1.2"

# Expose Spring Boot port
EXPOSE ${SERVER_PORT}

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Ensure uploads directory exists and can be mounted as a volume
RUN mkdir -p ${APP_UPLOADS_DESIGNS_DIR}
VOLUME ["/app/uploads"]

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
