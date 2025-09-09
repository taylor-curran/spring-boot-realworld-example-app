#!/bin/bash

# Cloud Foundry deployment script for RealWorld Spring Boot App
set -e

echo "🚀 Deploying RealWorld Spring Boot App to Cloud Foundry..."

# Build the application
echo "📦 Building application..."
cd ..
./gradlew clean build -x test

# Create services if they don't exist
echo "🗄️ Creating database service..."
cf create-service elephantsql turtle realworld-postgres-db || echo "Service already exists"

# Deploy the application
echo "☁️ Pushing application to Cloud Foundry..."
cd cloudfoundry
cf push -f manifest.yml

# Bind services
echo "🔗 Binding services..."
cf bind-service spring-boot-realworld-app realworld-postgres-db

# Restart to pick up service bindings
echo "🔄 Restarting application..."
cf restart spring-boot-realworld-app

# Show application info
echo "ℹ️ Application status:"
cf app spring-boot-realworld-app

echo "✅ Deployment complete!"
echo "🌐 Access your app at: https://spring-boot-realworld-app.cfapps.io"
