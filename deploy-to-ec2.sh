#!/bin/bash

# Deployment script for GymCRM with RDS integration
echo "Deploying GymCRM application to EC2 with RDS integration..."

# Build the application
echo "Building application..."
./build.sh

# Export Docker image to tar file for upload to S3
echo "Exporting Docker image..."
docker save gymcrm-rds:latest | gzip > gymcrm-rds-latest.tar.gz

echo "Docker image exported to gymcrm-rds-latest.tar.gz"
echo ""
echo "Next steps:"
echo "1. Upload gymcrm-rds-latest.tar.gz to your S3 bucket"
echo "2. Update your AMI with the new Docker image"
echo "3. Launch EC2 instance with the updated Docker image"
echo ""
echo "RDS Configuration:"
echo "✓ Database URL: gymcrm-rds.cw168m0ycth1.us-east-1.rds.amazonaws.com:3306"
echo "✓ Username: admin"
echo "✓ Password: Configured in application-prod.properties"
echo "✓ SSL/TLS: Enabled with AWS RDS CA certificates"
echo ""
echo "Environment Variables for EC2 (minimal required):"
echo "SPRING_PROFILES_ACTIVE=prod"
echo "AWS_REGION=us-east-1"
echo ""
echo "For IAM Authentication (optional), also set:"
echo "IAM_DB_USER=<your-iam-db-user>"
echo ""
echo "AWS CLI commands to upload to S3:"
echo "aws s3 cp gymcrm-rds-latest.tar.gz s3://your-bucket-name/"
echo ""
echo "To run the container on EC2:"
echo "docker load -i gymcrm-rds-latest.tar.gz"
echo "docker run -d -p 8080:8080 \\"
echo "  -e AWS_REGION=us-east-1 \\"
echo "  --name gymcrm-app \\"
echo "  gymcrm-rds:latest" 