FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /app

# Copy maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables for disabled integrations
ENV SPRING_PROFILES_ACTIVE=local
ENV EUREKA_CLIENT_ENABLED=false
ENV SPRING_ACTIVEMQ_BROKER_URL=disabled

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]