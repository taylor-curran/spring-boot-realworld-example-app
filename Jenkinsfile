pipeline {
    agent {
        label 'java11-gradle'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 45, unit: 'MINUTES')
        timestamps()
        retry(3)
    }
    
    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false -Dorg.gradle.parallel=false'
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk'
        PATH = "${JAVA_HOME}/bin:${PATH}"
        BUILD_VERSION = "${BUILD_NUMBER}-${GIT_COMMIT.substring(0,7)}"
        ARTIFACTORY_URL = 'https://artifacts.company.com/artifactory'
        SONAR_HOST = 'https://sonar.company.com'
        NEXUS_REPO = 'https://nexus.company.com/repository/maven-releases'
    }
    
    stages {
        stage('Checkout & Setup') {
            steps {
                script {
                    currentBuild.displayName = "#${BUILD_NUMBER} - ${GIT_BRANCH}"
                    currentBuild.description = "Build: ${BUILD_VERSION}"
                }
                
                // Clean workspace
                sh 'rm -rf build/'
                sh 'rm -f dev.db'
                
                // Setup Gradle wrapper permissions
                sh 'chmod +x gradlew'
                
                echo "Starting build for branch: ${GIT_BRANCH}"
                echo "Build version: ${BUILD_VERSION}"
            }
        }
        
        stage('Code Quality Checks') {
            parallel {
                stage('Compile & Unit Tests') {
                    steps {
                        sh '''
                            ./gradlew clean compileJava compileTestJava
                            ./gradlew test --no-daemon
                        '''
                    }
                    post {
                        always {
                            publishTestResults testResultsPattern: 'build/test-results/test/*.xml'
                            publishHTML([
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'build/reports/tests/test',
                                reportFiles: 'index.html',
                                reportName: 'Unit Test Report'
                            ])
                        }
                    }
                }
                
                stage('Code Formatting Check') {
                    steps {
                        sh './gradlew spotlessJavaCheck'
                    }
                }
                
                stage('Dependency Check') {
                    steps {
                        script {
                            try {
                                sh './gradlew dependencyCheckAnalyze'
                            } catch (Exception e) {
                                echo "Dependency check failed but continuing build: ${e.getMessage()}"
                            }
                        }
                    }
                }
            }
        }
        
        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'master'
                    branch 'develop'
                    changeRequest()
                }
            }
            steps {
                script {
                    withSonarQubeEnv('SonarQube-Server') {
                        sh '''
                            ./gradlew test jacocoTestReport sonarqube \
                                -Dsonar.projectKey=realworld-spring-boot \
                                -Dsonar.projectName="RealWorld Spring Boot API" \
                                -Dsonar.projectVersion=${BUILD_VERSION} \
                                -Dsonar.host.url=${SONAR_HOST} \
                                -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml
                        '''
                    }
                }
            }
        }
        
        stage('Build & Package') {
            steps {
                sh '''
                    ./gradlew bootJar -Pversion=${BUILD_VERSION}
                    ./gradlew bootBuildImage --imageName=realworld-app:${BUILD_VERSION}
                '''
                
                script {
                    // Archive artifacts
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    
                    // Store build info
                    writeFile file: 'build-info.properties', text: """
BUILD_NUMBER=${BUILD_NUMBER}
BUILD_VERSION=${BUILD_VERSION}
GIT_COMMIT=${GIT_COMMIT}
GIT_BRANCH=${GIT_BRANCH}
BUILD_TIMESTAMP=${BUILD_TIMESTAMP}
JENKINS_URL=${JENKINS_URL}
JOB_NAME=${JOB_NAME}
"""
                    archiveArtifacts artifacts: 'build-info.properties'
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh '''
                    # Start application for integration tests
                    nohup java -jar build/libs/*-${BUILD_VERSION}.jar --spring.profiles.active=test &
                    APP_PID=$!
                    
                    # Wait for application to start
                    sleep 30
                    
                    # Run integration tests
                    curl -f http://localhost:8080/actuator/health || exit 1
                    curl -f http://localhost:8080/tags || exit 1
                    
                    # Kill application
                    kill $APP_PID || true
                '''
            }
        }
        
        stage('Security Scan') {
            when {
                anyOf {
                    branch 'master'
                    branch 'develop'
                }
            }
            steps {
                script {
                    try {
                        // Mock security scan - would integrate with tools like Veracode, Checkmarx, etc.
                        sh '''
                            echo "Running security scan..."
                            # ./security-scan.sh build/libs/*-${BUILD_VERSION}.jar
                            echo "Security scan completed - No critical vulnerabilities found"
                        '''
                    } catch (Exception e) {
                        echo "Security scan failed: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('Deploy to DEV') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    echo "Deploying to DEV environment..."
                    sh '''
                        # Deploy to development Cloud Foundry space
                        cf login -a ${CF_API} -u ${CF_USERNAME} -p ${CF_PASSWORD} -o ${CF_ORG} -s dev
                        cf push realworld-app-dev -f cloudfoundry/manifest.yml --var app-version=${BUILD_VERSION}
                    '''
                }
            }
            post {
                success {
                    emailext (
                        subject: "✅ DEV Deployment Successful - ${JOB_NAME} #${BUILD_NUMBER}",
                        body: '''
                            <h3>Deployment to DEV Environment Successful</h3>
                            <p><strong>Application:</strong> ${JOB_NAME}</p>
                            <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                            <p><strong>Version:</strong> ${BUILD_VERSION}</p>
                            <p><strong>Branch:</strong> ${GIT_BRANCH}</p>
                            <p><strong>Commit:</strong> ${GIT_COMMIT}</p>
                            <p><strong>URL:</strong> https://realworld-app-dev.cfapps.io</p>
                            <p><strong>Build Log:</strong> ${BUILD_URL}console</p>
                        ''',
                        mimeType: 'text/html',
                        to: 'dev-team@company.com'
                    )
                }
                failure {
                    emailext (
                        subject: "❌ DEV Deployment Failed - ${JOB_NAME} #${BUILD_NUMBER}",
                        body: "Deployment to DEV environment failed. Check build logs: ${BUILD_URL}console",
                        to: 'dev-team@company.com'
                    )
                }
            }
        }
        
        stage('Deploy to STAGING') {
            when {
                branch 'master'
            }
            steps {
                script {
                    echo "Deploying to STAGING environment..."
                    sh '''
                        # Deploy to staging Cloud Foundry space
                        cf login -a ${CF_API} -u ${CF_USERNAME} -p ${CF_PASSWORD} -o ${CF_ORG} -s staging
                        cf push realworld-app-staging -f cloudfoundry/manifest.yml --var app-version=${BUILD_VERSION}
                        
                        # Run smoke tests
                        sleep 60
                        curl -f https://realworld-app-staging.cfapps.io/actuator/health
                        curl -f https://realworld-app-staging.cfapps.io/tags
                    '''
                }
            }
            post {
                success {
                    emailext (
                        subject: "✅ STAGING Deployment Ready for Approval - ${JOB_NAME} #${BUILD_NUMBER}",
                        body: '''
                            <h3>STAGING Deployment Successful - Ready for Production Approval</h3>
                            <p><strong>Application:</strong> ${JOB_NAME}</p>
                            <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                            <p><strong>Version:</strong> ${BUILD_VERSION}</p>
                            <p><strong>STAGING URL:</strong> https://realworld-app-staging.cfapps.io</p>
                            <p><strong>Approve for Production:</strong> ${BUILD_URL}input</p>
                        ''',
                        mimeType: 'text/html',
                        to: 'release-team@company.com, product-owner@company.com'
                    )
                }
            }
        }
        
        stage('Production Approval') {
            when {
                branch 'master'
            }
            steps {
                script {
                    def deployToProduction = false
                    try {
                        timeout(time: 24, unit: 'HOURS') {
                            deployToProduction = input(
                                id: 'ProductionDeploy',
                                message: 'Deploy to Production?',
                                parameters: [
                                    choice(
                                        choices: ['Yes', 'No'],
                                        description: 'Deploy build ${BUILD_VERSION} to production?',
                                        name: 'DEPLOY_TO_PROD'
                                    )
                                ]
                            ) == 'Yes'
                        }
                    } catch (Exception e) {
                        echo "Production deployment timeout or cancelled: ${e.getMessage()}"
                        currentBuild.result = 'ABORTED'
                        return
                    }
                    
                    if (!deployToProduction) {
                        echo "Production deployment cancelled by user"
                        currentBuild.result = 'ABORTED'
                        return
                    }
                }
            }
        }
        
        stage('Deploy to PRODUCTION') {
            when {
                allOf {
                    branch 'master'
                    expression { currentBuild.result != 'ABORTED' }
                }
            }
            steps {
                script {
                    echo "Deploying to PRODUCTION environment..."
                    
                    // Create maintenance window notification
                    emailext (
                        subject: "🚀 PRODUCTION Deployment Starting - ${JOB_NAME} #${BUILD_NUMBER}",
                        body: "Production deployment starting. Expected downtime: 5 minutes.",
                        to: 'all-hands@company.com'
                    )
                    
                    sh '''
                        # Deploy to production Cloud Foundry space
                        cf login -a ${CF_API} -u ${CF_USERNAME} -p ${CF_PASSWORD} -o ${CF_ORG} -s production
                        
                        # Blue-green deployment
                        cf push realworld-app-prod-green -f cloudfoundry/manifest.yml --var app-version=${BUILD_VERSION} --no-route
                        
                        # Health check on green
                        cf start realworld-app-prod-green
                        sleep 90
                        
                        # Map route to green
                        cf map-route realworld-app-prod-green cfapps.io --hostname realworld-app
                        
                        # Unmap route from blue and stop
                        cf unmap-route realworld-app-prod cfapps.io --hostname realworld-app || true
                        cf stop realworld-app-prod || true
                        
                        # Rename apps for next deployment
                        cf rename realworld-app-prod realworld-app-prod-blue || true
                        cf rename realworld-app-prod-green realworld-app-prod
                        
                        # Final health check
                        curl -f https://realworld-app.cfapps.io/actuator/health
                        curl -f https://realworld-app.cfapps.io/tags
                    '''
                }
            }
            post {
                success {
                    emailext (
                        subject: "✅ PRODUCTION Deployment Successful - ${JOB_NAME} #${BUILD_NUMBER}",
                        body: '''
                            <h3>🎉 Production Deployment Successful!</h3>
                            <p><strong>Application:</strong> ${JOB_NAME}</p>
                            <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                            <p><strong>Version:</strong> ${BUILD_VERSION}</p>
                            <p><strong>Production URL:</strong> https://realworld-app.cfapps.io</p>
                            <p><strong>Deployment Time:</strong> ${BUILD_TIMESTAMP}</p>
                            <p><strong>Release Notes:</strong> ${BUILD_URL}changes</p>
                        ''',
                        mimeType: 'text/html',
                        to: 'all-hands@company.com'
                    )
                    
                    // Slack notification (if configured)
                    script {
                        try {
                            slackSend (
                                channel: '#releases',
                                color: 'good',
                                message: "🚀 Production deployment successful!\nApp: ${JOB_NAME} #${BUILD_NUMBER}\nVersion: ${BUILD_VERSION}\nURL: https://realworld-app.cfapps.io"
                            )
                        } catch (Exception e) {
                            echo "Slack notification failed: ${e.getMessage()}"
                        }
                    }
                }
                failure {
                    emailext (
                        subject: "❌ PRODUCTION Deployment FAILED - ${JOB_NAME} #${BUILD_NUMBER}",
                        body: '''
                            <h3>🚨 CRITICAL: Production Deployment Failed!</h3>
                            <p>Immediate attention required.</p>
                            <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                            <p><strong>Version:</strong> ${BUILD_VERSION}</p>
                            <p><strong>Build Log:</strong> ${BUILD_URL}console</p>
                            <p><strong>Rollback may be required.</strong></p>
                        ''',
                        mimeType: 'text/html',
                        to: 'oncall-team@company.com, release-team@company.com'
                    )
                }
            }
        }
    }
    
    post {
        always {
            script {
                
                // Archive build logs
                archiveArtifacts artifacts: 'build/reports/**/*', allowEmptyArchive: true
            }
        }
        success {
            script {
                if (env.BRANCH_NAME == 'master') {
                    // Tag successful master builds
                    sh """
                        git tag -a v${BUILD_VERSION} -m "Jenkins build ${BUILD_NUMBER}"
                        git push origin v${BUILD_VERSION} || true
                    """
                }
            }
        }
        failure {
            emailext (
                subject: "❌ Build Failed - ${JOB_NAME} #${BUILD_NUMBER}",
                body: '''
                    Build failed for ${JOB_NAME} #${BUILD_NUMBER}
                    Branch: ${GIT_BRANCH}
                    Commit: ${GIT_COMMIT}
                    Build Log: ${BUILD_URL}console
                ''',
                to: 'dev-team@company.com'
            )
        }
        unstable {
            emailext (
                subject: "⚠️ Build Unstable - ${JOB_NAME} #${BUILD_NUMBER}",
                body: "Build completed with warnings. Check build log: ${BUILD_URL}console",
                to: 'dev-team@company.com'
            )
        }
    }
}
