# Jenkins CI/CD Configuration

This directory contains the Jenkins CI/CD pipeline configuration for the RealWorld Spring Boot API application.

## Overview

The Jenkins setup includes:
- **Jenkinsfile** - Modern declarative pipeline with blue-green production deployments
- **Legacy Job Configuration** - XML-based freestyle job for teams not yet migrated to Pipeline as Code
- **Deployment Scripts** - Bash scripts for environment-specific deployments
- **Configuration Files** - Environment settings and build parameters

## Files Structure

```
jenkins/
├── README.md                 # This documentation
├── job-config.xml           # Legacy Jenkins freestyle job configuration
├── deploy-config.properties # Environment-specific deployment settings
├── deploy.sh               # Main deployment script with blue-green support
└── build-and-test.sh       # Legacy build and test script
```

## Pipeline Features

### Modern Pipeline (Jenkinsfile)
- **Multi-environment support**: DEV, STAGING, PRODUCTION
- **Blue-green deployments** for production
- **Manual approval gates** for production releases
- **Parallel execution** of code quality checks
- **SonarQube integration** for code quality analysis
- **Security scanning** and vulnerability checks
- **Email notifications** for deployment status
- **Slack integration** for team notifications
- **Artifact archiving** and build metadata

### Legacy Job Configuration (job-config.xml)
- **Freestyle Jenkins job** for traditional setups
- **Parameterized builds** with branch selection
- **SCM polling** and GitHub webhook triggers
- **JUnit test reporting** and code coverage
- **Email notifications** for build status
- **Downstream job triggering** for deployments

## Environment Configuration

### Development (DEV)
- **Automatic deployment** on `develop` branch
- **Single instance** with 1GB memory
- **PostgreSQL development database**
- **Email notifications** to dev team

### Staging (STAGING)
- **Automatic deployment** on `master` branch
- **2 instances** with 2GB memory each
- **Smoke tests** after deployment
- **Email notifications** to QA and release teams

### Production (PRODUCTION)
- **Manual approval required** after staging
- **Blue-green deployment** with 4 instances
- **4GB memory** per instance
- **Comprehensive health checks**
- **Rollback capability**
- **All-hands notifications**

## Setup Instructions

### 1. Jenkins Server Configuration

#### Required Plugins
```bash
# Install these Jenkins plugins:
- Pipeline
- Blue Ocean
- Email Extension
- JUnit
- Jacoco
- SonarQube Scanner
- Cloud Foundry Plugin
- Build Timeout
- Timestamper
- AnsiColor
- Parameterized Trigger
```

#### Environment Variables
```bash
# Set these in Jenkins global configuration:
CF_API=https://api.cf.company.com
CF_USERNAME=jenkins-deployer
CF_PASSWORD=<encrypted-password>
CF_ORG=engineering
SONAR_HOST=https://sonar.company.com
ARTIFACTORY_URL=https://artifacts.company.com/artifactory
```

### 2. Create Pipeline Job

#### Option A: Modern Pipeline
1. Create new Pipeline job in Jenkins
2. Configure SCM to point to this repository
3. Set Pipeline script path to `Jenkinsfile`
4. Configure webhooks for automatic triggering

#### Option B: Legacy Freestyle Job
1. Create new Freestyle job in Jenkins
2. Import configuration from `jenkins/job-config.xml`
3. Update repository URL and credentials
4. Configure build triggers and notifications

### 3. Configure Agents

#### Java 11 Agent Requirements
```bash
# Agent should have:
- Java 11 JDK
- Gradle (or use wrapper)
- Cloud Foundry CLI
- Git
- curl (for health checks)
```

#### Agent Labels
- `java11-gradle` - For modern pipeline
- `java11-gradle-agent` - For legacy jobs

### 4. Cloud Foundry Setup

#### Service Configuration
```bash
# Create required services in each CF space:

# Development
cf target -s dev
cf create-service elephantsql turtle realworld-postgres-dev

# Staging  
cf target -s staging
cf create-service postgres-service standard realworld-postgres-staging

# Production
cf target -s production
cf create-service postgres-service premium realworld-postgres-prod
```

#### User Permissions
```bash
# Jenkins service account needs:
- SpaceDeveloper role in all spaces
- OrgManager role for service creation
- Access to bind/unbind services
```

## Deployment Process

### Manual Deployment
```bash
# Deploy to development
cd jenkins
./deploy.sh dev

# Deploy to staging with specific version
./deploy.sh staging 123-abc1234

# Deploy to production (requires approval in pipeline)
./deploy.sh production 124-def5678
```

### Pipeline Deployment
1. **Developer pushes** to `develop` branch
2. **Pipeline triggers** automatically
3. **Build and test** stages execute in parallel
4. **SonarQube analysis** runs for quality checks
5. **DEV deployment** happens automatically
6. **Email notification** sent to dev team

For production:
1. **Developer pushes** to `master` branch
2. **Pipeline deploys** to staging automatically
3. **Smoke tests** verify staging deployment
4. **Manual approval** required for production
5. **Blue-green deployment** to production
6. **Health checks** and notifications

## Monitoring and Notifications

### Email Configuration
```properties
# Email templates configured for:
- Build failures
- Deployment successes/failures
- Production approval requests
- Security scan alerts
```

### Slack Integration
```bash
# Configured channels:
#releases - Production deployments
#builds - Build status updates
#alerts - Critical failures
```

### Health Checks
```bash
# Automated checks:
/actuator/health - Application health
/tags - API endpoint test
/api/profiles - Authentication test
```

## Troubleshooting

### Common Issues

#### Build Failures
1. Check Java version compatibility
2. Verify Gradle daemon settings
3. Review test failure logs
4. Check dependency conflicts

#### Deployment Failures
1. Verify CF credentials and permissions
2. Check service availability
3. Review manifest.yml configuration
4. Validate environment variables

#### Health Check Failures
1. Increase health check timeout
2. Verify database connectivity
3. Check application logs
4. Review security group settings

### Debug Commands
```bash
# Check CF connectivity
cf api
cf auth

# Verify application status
cf apps
cf services
cf logs <app-name> --recent

# Test endpoints manually
curl -v https://app-url/actuator/health
curl -v https://app-url/tags
```

## Legacy Integration Notes

This Jenkins setup maintains compatibility with:
- **Older Jenkins versions** (2.x series)
- **Traditional email notifications** (no modern chat tools)
- **Freestyle jobs** alongside pipelines
- **Manual approval processes** for compliance
- **Blue-green deployments** for zero-downtime
- **Legacy monitoring tools** and dashboards

## Security Considerations

- **Credentials stored** in Jenkins credential store
- **API keys encrypted** and rotated regularly  
- **Access logs** maintained for audit trails
- **Dependency scanning** integrated into pipeline
- **Container scanning** for security vulnerabilities
- **Network security** groups configured

## Migration Path

For teams wanting to modernize:
1. Start with Jenkinsfile pipeline
2. Migrate from freestyle jobs gradually
3. Add container-based deployments
4. Integrate modern monitoring tools
5. Implement GitOps workflows

## Support

- **DevOps Team**: devops@company.com
- **Release Manager**: release-manager@company.com
- **On-call Support**: oncall@company.com
- **Jenkins Admin**: jenkins-admin@company.com

---
*Last Updated: 2019-11-22*  
*Created for: Acme Corporation*  
*Application: RealWorld Spring Boot API*
