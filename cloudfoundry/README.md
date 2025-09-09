# Cloud Foundry Deployment

This directory contains Cloud Foundry deployment artifacts for the RealWorld Spring Boot application.

## Prerequisites

- Cloud Foundry CLI installed
- Access to a Cloud Foundry environment
- PostgreSQL service available in marketplace

## Quick Deployment

1. Build the application:
   ```bash
   ./gradlew clean build
   ```

2. Deploy using the script:
   ```bash
   cd cloudfoundry
   ./deploy.sh
   ```

## Manual Deployment

1. Create database service:
   ```bash
   cf create-service elephantsql turtle realworld-postgres-db
   ```

2. Push application:
   ```bash
   cf push -f manifest.yml
   ```

3. Bind services:
   ```bash
   cf bind-service spring-boot-realworld-app realworld-postgres-db
   ```

## Files

- `manifest.yml` - Application deployment configuration
- `.cfignore` - Files to exclude from deployment
- `Procfile` - Process types (optional for Spring Boot)
- `application-cloud.properties` - Cloud-specific configuration
- `deploy.sh` - Automated deployment script
- `services.yml` - Service definitions

## Environment Variables

Set these in your Cloud Foundry space:

- `JWT_SECRET` - JWT signing secret
- `SPRING_PROFILES_ACTIVE` - Should be set to 'cloud'

## Health Check

The application exposes health check endpoint at `/actuator/health`
