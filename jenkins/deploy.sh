#!/bin/bash

# Jenkins Legacy Deployment Script
# Company: Acme Corporation
# Application: RealWorld Spring Boot API
# Created: 2018-03-15
# Last Modified: 2019-11-22

set -e

# Load configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="${SCRIPT_DIR}/deploy-config.properties"

if [[ ! -f "$CONFIG_FILE" ]]; then
    echo "ERROR: Configuration file not found: $CONFIG_FILE"
    exit 1
fi

# Source configuration
source "$CONFIG_FILE"

# Default values
ENVIRONMENT="${1:-dev}"
BUILD_VERSION="${2:-latest}"
SKIP_TESTS="${3:-false}"

# Validate environment
if [[ ! "$environments" =~ $ENVIRONMENT ]]; then
    echo "ERROR: Invalid environment '$ENVIRONMENT'. Allowed values: $environments"
    exit 1
fi

# Colors for output (legacy terminal support)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    local level=$1
    shift
    local message="$*"
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    case $level in
        "ERROR")
            echo -e "${RED}[ERROR]${NC} ${timestamp} - $message" >&2
            ;;
        "WARN")
            echo -e "${YELLOW}[WARN]${NC} ${timestamp} - $message"
            ;;
        "INFO")
            echo -e "${BLUE}[INFO]${NC} ${timestamp} - $message"
            ;;
        "SUCCESS")
            echo -e "${GREEN}[SUCCESS]${NC} ${timestamp} - $message"
            ;;
    esac
}

# Email notification function
send_notification() {
    local subject="$1"
    local body="$2"
    local recipients="$3"
    
    if [[ "$email.enabled" == "true" ]]; then
        echo "Subject: $subject" > /tmp/deployment_email.txt
        echo "To: $recipients" >> /tmp/deployment_email.txt
        echo "" >> /tmp/deployment_email.txt
        echo "$body" >> /tmp/deployment_email.txt
        
        # Legacy sendmail command (would be replaced with actual SMTP in real environment)
        # sendmail -t < /tmp/deployment_email.txt
        log "INFO" "Email notification sent to: $recipients"
        log "INFO" "Subject: $subject"
    fi
}

# Pre-deployment checks
pre_deployment_checks() {
    log "INFO" "Starting pre-deployment checks..."
    
    # Check if Cloud Foundry CLI is installed
    if ! command -v cf &> /dev/null; then
        log "ERROR" "Cloud Foundry CLI not found. Please install cf CLI."
        exit 1
    fi
    
    # Check if required environment variables are set
    local cf_api_var="${ENVIRONMENT}.cf.api"
    local cf_org_var="${ENVIRONMENT}.cf.org"
    local cf_space_var="${ENVIRONMENT}.cf.space"
    
    if [[ -z "${!cf_api_var}" ]]; then
        log "ERROR" "CF API not configured for environment: $ENVIRONMENT"
        exit 1
    fi
    
    # Check if artifact exists
    local artifact_path="../build/libs"
    if [[ ! -d "$artifact_path" ]] || [[ -z "$(ls -A $artifact_path 2>/dev/null)" ]]; then
        log "ERROR" "No build artifacts found. Please run './gradlew build' first."
        exit 1
    fi
    
    log "SUCCESS" "Pre-deployment checks completed"
}

# Deploy to environment
deploy_to_environment() {
    local env="$1"
    
    log "INFO" "Starting deployment to $env environment..."
    log "INFO" "Build Version: $BUILD_VERSION"
    
    # Get environment-specific configuration
    local cf_api_var="${env}.cf.api"
    local cf_org_var="${env}.cf.org"
    local cf_space_var="${env}.cf.space"
    local app_name_var="${env}.app.name"
    local app_memory_var="${env}.app.memory"
    local app_instances_var="${env}.app.instances"
    local notification_emails_var="${env}.notification.emails"
    
    local cf_api="${!cf_api_var}"
    local cf_org="${!cf_org_var}"
    local cf_space="${!cf_space_var}"
    local app_name="${!app_name_var}"
    local app_memory="${!app_memory_var}"
    local app_instances="${!app_instances_var}"
    local notification_emails="${!notification_emails_var}"
    
    log "INFO" "Target: $cf_api ($cf_org/$cf_space)"
    log "INFO" "Application: $app_name"
    
    # Send deployment start notification
    send_notification \
        "🚀 Deployment Started - $app_name ($env)" \
        "Deployment to $env environment has started.
Build Version: $BUILD_VERSION
Target: $cf_org/$cf_space
Expected downtime: 2-5 minutes
Jenkins Job: $JOB_NAME #$BUILD_NUMBER" \
        "$notification_emails"
    
    # Login to Cloud Foundry
    log "INFO" "Logging into Cloud Foundry..."
    cf login -a "$cf_api" -u "$CF_USERNAME" -p "$CF_PASSWORD" -o "$cf_org" -s "$cf_space"
    
    # Create services if they don't exist (development only)
    if [[ "$env" == "dev" ]]; then
        log "INFO" "Creating/updating services for development environment..."
        local db_service_var="${env}.db.service"
        local db_service="${!db_service_var}"
        
        cf create-service elephantsql turtle "$db_service" || log "WARN" "Service $db_service might already exist"
    fi
    
    # Prepare deployment
    local manifest_file="../cloudfoundry/manifest.yml"
    local temp_manifest="/tmp/manifest-${env}-${BUILD_VERSION}.yml"
    
    # Copy and customize manifest for environment
    cp "$manifest_file" "$temp_manifest"
    
    # Replace placeholders (legacy sed approach)
    sed -i.bak "s/spring-boot-realworld-app/$app_name/g" "$temp_manifest"
    sed -i.bak "s/memory: 1G/memory: $app_memory/g" "$temp_manifest"
    sed -i.bak "s/instances: 1/instances: $app_instances/g" "$temp_manifest"
    
    # Deploy based on environment
    case $env in
        "production")
            deploy_production "$app_name" "$temp_manifest"
            ;;
        *)
            deploy_standard "$app_name" "$temp_manifest"
            ;;
    esac
    
    # Post-deployment health checks
    post_deployment_checks "$app_name" "$env"
    
    # Send success notification
    send_notification \
        "✅ Deployment Successful - $app_name ($env)" \
        "Deployment to $env environment completed successfully!
Build Version: $BUILD_VERSION
Application URL: https://$app_name.cfapps.io
Health Check: Passed
Jenkins Job: $JOB_NAME #$BUILD_NUMBER" \
        "$notification_emails"
    
    log "SUCCESS" "Deployment to $env completed successfully!"
}

# Standard deployment (dev/staging)
deploy_standard() {
    local app_name="$1"
    local manifest="$2"
    
    log "INFO" "Performing standard deployment..."
    
    # Push application
    cf push -f "$manifest" --var app-version="$BUILD_VERSION"
    
    # Bind services
    local db_service_var="${ENVIRONMENT}.db.service"
    local db_service="${!db_service_var}"
    cf bind-service "$app_name" "$db_service" || log "WARN" "Service binding might have failed"
    
    # Start application
    cf start "$app_name"
}

# Production blue-green deployment
deploy_production() {
    local app_name="$1"
    local manifest="$2"
    
    log "INFO" "Performing blue-green production deployment..."
    
    local green_app="${app_name}-green"
    local blue_app="${app_name}-blue"
    
    # Deploy to green
    log "INFO" "Deploying green version: $green_app"
    sed -i.bak "s/$app_name/$green_app/g" "$manifest"
    cf push -f "$manifest" --var app-version="$BUILD_VERSION" --no-route
    
    # Bind services to green
    local db_service_var="${ENVIRONMENT}.db.service"
    local db_service="${!db_service_var}"
    cf bind-service "$green_app" "$db_service"
    
    # Start green app
    cf start "$green_app"
    
    # Wait for green to be healthy
    log "INFO" "Waiting for green app to be healthy..."
    sleep "${bluegreen.health.check.delay:-90}"
    
    # Health check green
    local app_domain_var="${ENVIRONMENT}.app.domain"
    local app_domain="${!app_domain_var}"
    local green_url="https://${green_app}.${app_domain}"
    
    if curl -f "$green_url/actuator/health" > /dev/null 2>&1; then
        log "SUCCESS" "Green app health check passed"
        
        # Switch traffic to green
        log "INFO" "Switching traffic to green app..."
        cf map-route "$green_app" "$app_domain" --hostname "${app_name}"
        
        # Remove traffic from blue
        cf unmap-route "$app_name" "$app_domain" --hostname "${app_name}" || log "WARN" "Blue app might not exist"
        
        # Stop and rename apps
        cf stop "$app_name" || log "WARN" "Blue app might not exist"
        cf rename "$app_name" "$blue_app" || log "WARN" "Blue app might not exist"
        cf rename "$green_app" "$app_name"
        
        log "SUCCESS" "Blue-green deployment completed"
    else
        log "ERROR" "Green app health check failed - rolling back"
        cf stop "$green_app"
        cf delete "$green_app" -f
        exit 1
    fi
}

# Post-deployment health checks
post_deployment_checks() {
    local app_name="$1"
    local env="$2"
    
    log "INFO" "Running post-deployment health checks..."
    
    # Get app URL
    local app_domain_var="${env}.app.domain"
    local app_domain="${!app_domain_var}"
    local app_url="https://${app_name}.${app_domain}"
    
    # Health check endpoint
    local health_endpoint="${health.check.endpoint:-/actuator/health}"
    local health_url="${app_url}${health_endpoint}"
    
    local retries="${health.check.retries:-3}"
    local timeout="${health.check.timeout:-30}"
    
    for i in $(seq 1 $retries); do
        log "INFO" "Health check attempt $i/$retries: $health_url"
        
        if curl -f --max-time "$timeout" "$health_url" > /dev/null 2>&1; then
            log "SUCCESS" "Health check passed"
            break
        else
            if [[ $i -eq $retries ]]; then
                log "ERROR" "Health check failed after $retries attempts"
                return 1
            fi
            log "WARN" "Health check attempt $i failed, retrying..."
            sleep 10
        fi
    done
    
    # Smoke tests
    if [[ "$smoke.test.enabled" == "true" ]]; then
        log "INFO" "Running smoke tests..."
        
        local endpoints="${smoke.test.endpoints:-/actuator/health,/tags}"
        IFS=',' read -ra ENDPOINT_ARRAY <<< "$endpoints"
        
        for endpoint in "${ENDPOINT_ARRAY[@]}"; do
            local test_url="${app_url}${endpoint}"
            log "INFO" "Testing endpoint: $test_url"
            
            if curl -f --max-time 30 "$test_url" > /dev/null 2>&1; then
                log "SUCCESS" "Smoke test passed: $endpoint"
            else
                log "ERROR" "Smoke test failed: $endpoint"
                return 1
            fi
        done
    fi
    
    log "SUCCESS" "All post-deployment checks passed"
}

# Main execution
main() {
    echo "=========================================="
    echo "   Jenkins Legacy Deployment Script"
    echo "   Company: $(echo "$company.name")"
    echo "   Application: $(echo "$app.name")"
    echo "=========================================="
    echo ""
    
    log "INFO" "Starting deployment process..."
    log "INFO" "Environment: $ENVIRONMENT"
    log "INFO" "Build Version: $BUILD_VERSION"
    log "INFO" "Jenkins Job: ${JOB_NAME:-manual} #${BUILD_NUMBER:-0}"
    
    # Run pre-deployment checks
    pre_deployment_checks
    
    # Deploy to target environment
    deploy_to_environment "$ENVIRONMENT"
    
    log "SUCCESS" "Deployment process completed successfully!"
    echo ""
    echo "=========================================="
}

# Script usage
usage() {
    echo "Usage: $0 [ENVIRONMENT] [BUILD_VERSION] [SKIP_TESTS]"
    echo ""
    echo "Arguments:"
    echo "  ENVIRONMENT    Target environment (dev, staging, production) [default: dev]"
    echo "  BUILD_VERSION  Build version to deploy [default: latest]" 
    echo "  SKIP_TESTS     Skip tests (true/false) [default: false]"
    echo ""
    echo "Examples:"
    echo "  $0 dev"
    echo "  $0 staging 123-abc1234"
    echo "  $0 production 124-def5678 false"
    echo ""
    echo "Available environments: $environments"
}

# Handle script arguments
if [[ "$1" == "-h" ]] || [[ "$1" == "--help" ]]; then
    usage
    exit 0
fi

# Execute main function
main "$@"
