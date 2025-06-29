FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install wget and create directory for SSL certificates
RUN apt-get update && apt-get install -y wget && \
    mkdir -p /opt/mysql && \
    rm -rf /var/lib/apt/lists/*

# Download AWS RDS CA certificates
RUN wget https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem -O /opt/mysql/server-ca.pem

# Copy the pre-built fat JAR
COPY target/*.jar app.jar

# Set environment variables for RDS production
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_ENABLED=false

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
