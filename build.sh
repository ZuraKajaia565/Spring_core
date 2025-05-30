#!/bin/bash

# Build the fat JAR
echo "Building fat JAR..."
./mvnw clean package -DskipTests

# Build Docker images
echo "Building Docker images..."
docker compose build

echo "Build completed successfully!"