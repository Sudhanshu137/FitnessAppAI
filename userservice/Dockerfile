# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy jar
COPY target/*.jar app.jar

# Expose service port
EXPOSE 8082

# Run application with docker profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
