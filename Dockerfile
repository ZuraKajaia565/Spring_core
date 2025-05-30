FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the pre-built fat JAR
COPY target/*.jar app.jar

# Set environment variables for disabled integrations
ENV SPRING_PROFILES_ACTIVE=local
ENV EUREKA_CLIENT_ENABLED=false
ENV SPRING_ACTIVEMQ_BROKER_URL=disabled

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
