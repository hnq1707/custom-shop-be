# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Cài CA certificates để Java trust MongoDB Atlas SSL
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*

# Biến môi trường mặc định (có thể override qua .env hoặc docker run -e)
ENV SERVER_PORT=8080 \
    SPRING_APPLICATION_NAME=ecommerce_be \
    SPRING_DATA_MONGODB_URI="mongodb+srv://hnq1707:quyen177@cluster0.0phizh4.mongodb.net/mydb?authSource=admin" \
    SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB \
    SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB \
    APP_UPLOADS_DESIGNS_DIR="/app/uploads/designs" \
    APP_AI_OPENAI_API_KEY="" \
    APP_AI_HF_API_KEY="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" \
    APP_AI_PROVIDER="hf" \
    APP_AI_HF_TEXT2IMG_MODEL="stabilityai/stable-diffusion-xl-base-1.0" \
    APP_CORS_ALLOWED_ORIGINS="*" \
    LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG \
    LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG

# Expose Spring Boot port
EXPOSE ${SERVER_PORT}

# Copy jar từ build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Tạo uploads folder mountable
RUN mkdir -p ${APP_UPLOADS_DESIGNS_DIR}
VOLUME ["/app/uploads"]

# CMD array an toàn: Java trực tiếp, không qua sh
CMD ["java", "-Dhttps.protocols=TLSv1.2", "-jar", "/app/app.jar"]
