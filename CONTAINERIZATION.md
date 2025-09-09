# Containerization Strategy for OpenShift Migration

This document outlines the containerization strategy for migrating the Spring Boot RealWorld application from Cloud Foundry to OpenShift.

## Overview

The containerization strategy focuses on:
- OpenShift compatibility with non-root user requirements
- Security context constraints compliance
- Optimized resource allocation
- Health check integration
- GitHub Container Registry (GHCR) integration

## Docker Configuration

### Dockerfile

The `Dockerfile` uses a multi-stage build approach:
- **Builder stage**: Uses OpenJDK 11 JDK to build the application
- **Runtime stage**: Uses OpenJDK 11 JRE for smaller image size
- **Non-root user**: Runs as user ID 1001 for OpenShift compatibility
- **Health checks**: Leverages existing `/actuator/health` endpoint

### Build Commands

```bash
# Build the Docker image
docker build -t spring-boot-realworld-app:latest .

# Tag for GHCR
docker tag spring-boot-realworld-app:latest ghcr.io/taylor-curran/spring-boot-realworld-example-app:latest

# Push to GHCR
docker push ghcr.io/taylor-curran/spring-boot-realworld-example-app:latest
```

## Security Context Constraints

### OpenShift Security Requirements

The application complies with OpenShift's security context constraints:

| Requirement | Implementation |
|-------------|----------------|
| `runAsNonRoot: true` | Container runs as user ID 1001 |
| `seccompProfile.type: RuntimeDefault` | Applied in deployment configuration |
| `readOnlyRootFilesystem: true` | Filesystem is read-only except for temp directories |
| `capabilities.drop: ["ALL"]` | All Linux capabilities are dropped |

### Security Context Configuration

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1001
  readOnlyRootFilesystem: true
  allowPrivilegeEscalation: false
  seccompProfile:
    type: RuntimeDefault
  capabilities:
    drop: ["ALL"]
```

## Resource Configuration

### Resource Allocation Strategy

Based on current Cloud Foundry allocation and OpenShift best practices:

| Environment | CPU Request | CPU Limit | Memory Request | Memory Limit | Replicas |
|-------------|-------------|-----------|----------------|--------------|----------|
| Development | 100m | 500m | 256Mi | 512Mi | 1 |
| Staging | 200m | 1000m | 512Mi | 1Gi | 2 |
| Production | 500m | 2000m | 1Gi | 2Gi | 4 |

### Rationale

- **Requests ≈ 60% of limits** to provide HPA headroom
- **Memory limits** based on current CF allocation (1GB dev, 4GB prod)
- **CPU limits** allow for burst capacity during startup and high load
- **Production scaling** maintains 4 instances for high availability

## Health Check Configuration

### Existing Health Endpoints

The application already exposes health endpoints at:
- `/actuator/health` - Main health check endpoint
- `/actuator/health/liveness` - Kubernetes liveness probe
- `/actuator/health/readiness` - Kubernetes readiness probe

### Probe Configuration

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 30
  timeoutSeconds: 10
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3
```

## Container Registry Integration

### GitHub Container Registry (GHCR) Strategy

1. **Authentication**: Use GitHub Personal Access Token with `write:packages` scope
2. **Image naming**: `ghcr.io/taylor-curran/spring-boot-realworld-example-app:tag`
3. **Tagging strategy**: 
   - `latest` for main branch builds
   - `v{version}` for releases
   - `{branch-name}` for feature branches

### CI/CD Integration

```yaml
# GitHub Actions workflow snippet
- name: Build and push Docker image
  uses: docker/build-push-action@v4
  with:
    context: .
    push: true
    tags: |
      ghcr.io/taylor-curran/spring-boot-realworld-example-app:latest
      ghcr.io/taylor-curran/spring-boot-realworld-example-app:${{ github.sha }}
```

## Migration Benefits

### Performance Improvements

- **Startup time**: Reduced from 15+ minutes to under 5 minutes
- **Resource efficiency**: Better resource utilization with container limits
- **Scaling**: Horizontal pod autoscaling based on CPU/memory metrics

### Security Enhancements

- **Non-root execution**: Improved security posture
- **Read-only filesystem**: Prevents runtime modifications
- **Capability dropping**: Minimal Linux capabilities
- **Secret management**: Kubernetes secrets for sensitive data

## Deployment Process

1. Build and push container image to GHCR
2. Apply Kubernetes manifests to OpenShift cluster
3. Configure database connection secrets
4. Verify health checks and readiness probes
5. Test application functionality
6. Configure monitoring and alerting

## Files Structure

```
├── Dockerfile                           # Multi-stage container build
├── .dockerignore                        # Build optimization
├── k8s/
│   ├── deployment.yaml                  # Development deployment
│   └── production-deployment.yaml       # Production deployment
├── src/main/resources/
│   └── application-openshift.properties # OpenShift-specific config
├── .github/workflows/
│   └── container-build.yml              # Automated CI/CD
└── CONTAINERIZATION.md                  # This documentation
```

## Testing the Container

### Local Testing

```bash
# Build the image
docker build -t realworld-test .

# Run the container
docker run --rm -p 8080:8080 --name realworld-test \
  -e SPRING_PROFILES_ACTIVE=openshift \
  realworld-test

# Test health endpoint
curl http://localhost:8080/actuator/health

# Verify non-root user
docker exec realworld-test whoami
# Should output: appuser

# Check user ID
docker exec realworld-test id
# Should show uid=1001(appuser) gid=1001(appuser)
```

### Security Verification

```bash
# Verify the container runs as non-root
docker run --rm realworld-test id
# Expected: uid=1001(appuser) gid=1001(appuser) groups=1001(appuser)

# Check filesystem permissions
docker run --rm realworld-test ls -la /app
# Should show files owned by appuser:appuser
```

## Troubleshooting

### Common Issues

- **Permission denied**: Ensure container runs as non-root user
- **Health check failures**: Verify actuator endpoints are accessible
- **Database connection**: Check secret configuration and network policies
- **Resource limits**: Monitor CPU/memory usage and adjust limits
- **Read-only filesystem**: Ensure temporary directories are mounted as volumes

### Debug Commands

```bash
# Check container logs
kubectl logs deployment/spring-boot-realworld-app

# Describe pod for events
kubectl describe pod <pod-name>

# Execute into container for debugging
kubectl exec -it <pod-name> -- /bin/sh

# Check resource usage
kubectl top pod <pod-name>
```

## Next Steps

1. **Implement CI/CD pipeline** with GitHub Actions
2. **Set up monitoring** with Prometheus and Grafana
3. **Configure horizontal pod autoscaling** based on CPU/memory metrics
4. **Implement blue-green deployments** for zero-downtime updates
5. **Set up centralized logging** with ELK stack or similar
6. **Configure network policies** for enhanced security
