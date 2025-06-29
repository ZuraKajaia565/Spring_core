#!/bin/bash

echo "Building GymCRM application with RDS support..."

# Clean and build the application
mvn clean package -DskipTests

# Build Docker image for RDS deployment
docker build -t gymcrm-rds:latest .

echo "Build completed successfully!"
echo "Docker image 'gymcrm-rds:latest' is ready for deployment to AWS EC2 with RDS integration"