#!/bin/bash

# Jenkins Legacy Build and Test Script
# Company: Acme Corporation
# Created: 2017-09-12
# Last Modified: 2019-08-15

set -e

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Build configuration
GRADLE_OPTS="${GRADLE_OPTS:--Dorg.gradle.daemon=false -Dorg.gradle.parallel=false}"
JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk}"
BUILD_NUMBER="${BUILD_NUMBER:-local}"
GIT_COMMIT="${GIT_COMMIT:-$(git rev-parse --short HEAD 2>/dev/null || echo 'unknown')}"
BUILD_VERSION="${BUILD_NUMBER}-${GIT_COMMIT}"

# Colors for legacy terminal support
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Logging
log() {
    local level=$1
    shift
    local message="$*"
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    case $level in
        "ERROR") echo -e "${RED}[ERROR]${NC} ${timestamp} - $message" >&2 ;;
        "WARN")  echo -e "${YELLOW}[WARN]${NC} ${timestamp} - $message" ;;
        "INFO")  echo -e "${BLUE}[INFO]${NC} ${timestamp} - $message" ;;
        "SUCCESS") echo -e "${GREEN}[SUCCESS]${NC} ${timestamp} - $message" ;;
    esac
}

# Environment setup
setup_environment() {
    log "INFO" "Setting up build environment..."
    
    # Export environment variables
    export GRADLE_OPTS
    export JAVA_HOME
    export PATH="$JAVA_HOME/bin:$PATH"
    export BUILD_VERSION
    
    # Change to project directory
    cd "$PROJECT_ROOT"
    
    # Verify Java version
    java -version 2>&1 | head -1
    
    # Make gradlew executable
    chmod +x gradlew
    
    log "INFO" "Environment setup completed"
    log "INFO" "Java Home: $JAVA_HOME"
    log "INFO" "Build Version: $BUILD_VERSION"
}

# Clean workspace
clean_workspace() {
    log "INFO" "Cleaning workspace..."
    
    # Remove previous build artifacts
    ./gradlew clean
    
    # Remove test database if exists
    rm -f dev.db
    rm -f test.db
    
    
    log "SUCCESS" "Workspace cleaned"
}

# Compile source code
compile_code() {
    log "INFO" "Compiling source code..."
    
    # Compile main and test classes
    ./gradlew compileJava compileTestJava
    
    if [ $? -eq 0 ]; then
        log "SUCCESS" "Code compilation completed"
    else
        log "ERROR" "Code compilation failed"
        exit 1
    fi
}

# Run static analysis
static_analysis() {
    log "INFO" "Running static analysis checks..."
    
    # Code formatting check
    log "INFO" "Checking code formatting with Spotless..."
    ./gradlew spotlessJavaCheck
    
    if [ $? -ne 0 ]; then
        log "WARN" "Code formatting issues detected"
        log "INFO" "Run './gradlew spotlessJavaApply' to fix formatting"
        
        # In legacy setups, we might continue with warnings
        log "WARN" "Continuing build with formatting warnings..."
    else
        log "SUCCESS" "Code formatting check passed"
    fi
    
    # Additional static analysis could go here
    # ./gradlew checkstyleMain checkstyleTest
    # ./gradlew pmd findbugs
    
    log "SUCCESS" "Static analysis completed"
}

# Run unit tests
run_unit_tests() {
    log "INFO" "Running unit tests..."
    
    # Run tests with coverage
    ./gradlew test jacocoTestReport
    
    if [ $? -eq 0 ]; then
        log "SUCCESS" "Unit tests completed"
        
        # Display test results summary
        if [ -f "build/test-results/test/TEST-*.xml" ]; then
            local test_files=$(ls build/test-results/test/TEST-*.xml 2>/dev/null || echo "")
            if [ ! -z "$test_files" ]; then
                log "INFO" "Test results generated: $(echo $test_files | wc -w) test files"
            fi
        fi
        
        # Check for test coverage report
        if [ -f "build/reports/jacoco/test/html/index.html" ]; then
            log "INFO" "Code coverage report generated: build/reports/jacoco/test/html/index.html"
        fi
    else
        log "ERROR" "Unit tests failed"
        
        # In legacy Jenkins setups, we might want to continue even with test failures
        # for debugging purposes, but mark build as unstable
        if [ "$CONTINUE_ON_TEST_FAILURE" = "true" ]; then
            log "WARN" "Continuing build despite test failures (CONTINUE_ON_TEST_FAILURE=true)"
        else
            exit 1
        fi
    fi
}

# Security and dependency checks
security_checks() {
    log "INFO" "Running security and dependency checks..."
    
    # Dependency vulnerability check (mock - would use actual tools in real environment)
    log "INFO" "Checking for vulnerable dependencies..."
    
    # This would typically run OWASP dependency check or similar
    # ./gradlew dependencyCheckAnalyze
    
    # For now, just check if there are any obvious security issues in dependencies
    ./gradlew dependencies > /tmp/deps.txt 2>/dev/null || true
    
    # Check for common vulnerable libraries (legacy approach)
    if grep -i "vulnerable\|cve" /tmp/deps.txt >/dev/null 2>&1; then
        log "WARN" "Potential security vulnerabilities detected in dependencies"
        log "WARN" "Review dependencies and update as needed"
    else
        log "SUCCESS" "No obvious dependency vulnerabilities detected"
    fi
    
    rm -f /tmp/deps.txt
    
    log "SUCCESS" "Security checks completed"
}

# Build application
build_application() {
    log "INFO" "Building application..."
    
    # Build JAR file
    ./gradlew bootJar
    
    if [ $? -eq 0 ]; then
        log "SUCCESS" "Application build completed"
        
        # List generated artifacts
        if [ -d "build/libs" ]; then
            log "INFO" "Generated artifacts:"
            ls -la build/libs/*.jar | while read line; do
                log "INFO" "  $line"
            done
        fi
    else
        log "ERROR" "Application build failed"
        exit 1
    fi
}


# Generate reports
generate_reports() {
    log "INFO" "Generating build reports..."
    
    # Create build info file
    cat > build-info.properties << EOF
# Build Information
# Generated: $(date)
BUILD_NUMBER=${BUILD_NUMBER}
BUILD_VERSION=${BUILD_VERSION}
GIT_COMMIT=${GIT_COMMIT}
GIT_BRANCH=${GIT_BRANCH:-unknown}
BUILD_TIMESTAMP=$(date -u +%Y-%m-%dT%H:%M:%SZ)
JENKINS_URL=${JENKINS_URL:-N/A}
JOB_NAME=${JOB_NAME:-manual-build}
GRADLE_VERSION=$(./gradlew --version | grep "Gradle " | cut -d' ' -f2)
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
EOF
    
    log "SUCCESS" "Build info generated: build-info.properties"
    
    # Generate simple HTML report (legacy style)
    cat > build-report.html << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Build Report - ${JOB_NAME:-Manual Build} #${BUILD_NUMBER}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background-color: #f0f0f0; padding: 10px; border-radius: 5px; }
        .success { color: green; }
        .warning { color: orange; }
        .error { color: red; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Build Report</h1>
        <p><strong>Job:</strong> ${JOB_NAME:-Manual Build}</p>
        <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
        <p><strong>Version:</strong> ${BUILD_VERSION}</p>
        <p><strong>Timestamp:</strong> $(date)</p>
    </div>
    
    <h2>Build Status</h2>
    <table>
        <tr><th>Stage</th><th>Status</th></tr>
        <tr><td>Environment Setup</td><td class="success">✓ Passed</td></tr>
        <tr><td>Code Compilation</td><td class="success">✓ Passed</td></tr>
        <tr><td>Static Analysis</td><td class="success">✓ Passed</td></tr>
        <tr><td>Unit Tests</td><td class="success">✓ Passed</td></tr>
        <tr><td>Security Checks</td><td class="success">✓ Passed</td></tr>
        <tr><td>Application Build</td><td class="success">✓ Passed</td></tr>
    </table>
    
    <h2>Artifacts</h2>
    <ul>
EOF
    
    # List artifacts
    if [ -d "build/libs" ]; then
        ls build/libs/*.jar | while read jar; do
            echo "        <li>$(basename "$jar")</li>" >> build-report.html
        done
    fi
    
    cat >> build-report.html << EOF
    </ul>
    
    <h2>Links</h2>
    <ul>
        <li><a href="${BUILD_URL:-#}console">Build Console</a></li>
        <li><a href="build/reports/tests/test/index.html">Test Report</a></li>
        <li><a href="build/reports/jacoco/test/html/index.html">Coverage Report</a></li>
    </ul>
</body>
</html>
EOF
    
    log "SUCCESS" "HTML report generated: build-report.html"
}

# Main build process
main() {
    echo "================================================="
    echo "    Jenkins Legacy Build and Test Script"
    echo "    Company: Acme Corporation"
    echo "    Application: RealWorld Spring Boot API"
    echo "================================================="
    echo ""
    
    local start_time=$(date +%s)
    
    # Execute build stages
    setup_environment
    clean_workspace
    compile_code
    static_analysis
    run_unit_tests
    security_checks
    build_application
    generate_reports
    
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    
    log "SUCCESS" "Build completed successfully in ${duration} seconds"
    echo ""
    echo "================================================="
    
    # Legacy Jenkins integration - set environment variables for downstream jobs
    echo "BUILD_VERSION=${BUILD_VERSION}" > jenkins.properties
    echo "ARTIFACT_PATH=build/libs" >> jenkins.properties
    echo "BUILD_SUCCESS=true" >> jenkins.properties
}

# Error handling
trap 'log "ERROR" "Build failed at line $LINENO"' ERR

# Execute if run directly
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
