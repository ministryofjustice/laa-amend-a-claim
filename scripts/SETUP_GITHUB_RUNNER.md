# GitHub Self-Hosted Runner Scripts

This directory contains an entry runner script and Dockerfiles for building and running a self-hosted GitHub Actions runner for E2E tests in Kubernetes.

## Overview

The self-hosted runner is designed to run Playwright-based E2E tests within the Kubernetes cluster, allowing direct access to internal services (data-claims-api, laa-amend-a-claim, postgres) without exposing them externally.

## Files

### Dockerfile.e2e-runner

Builds a Docker image containing:
- **GitHub Actions Runner** (v2.331.0) - Executes GitHub workflow jobs
- **Java 21 (Amazon Corretto)** - Required for running the E2E test suite
- **Playwright dependencies** - Browser automation libraries for Chromium/Firefox/WebKit
- **Gradle** - Build tool for the Java E2E tests

**Key Features:**
- Runs as non-root user `runner` (UID 1000) for security
- Automatic runner registration with GitHub repository
- Graceful cleanup on shutdown (removes runner from GitHub)
- Unique runner names to prevent conflicts in multi-pod deployments

**Environment Variables:**
- `GITHUB_TOKEN` (required) - GitHub runner registration token
- `GITHUB_REPOSITORY` (required) - Format: `owner/repo` (e.g., `ministryofjustice/laa-amend-a-claim`)
- `RUNNER_NAME` (optional) - Defaults to `k8s-e2e-runner` 
- `RUNNER_LABELS` (optional) - Defaults to `self-hosted,linux, abc-e2e`

Generate a GitHub Personal Access Token (PAT) with these scopes:
- `repo` - Full control of private repositories
- `workflow` - Update GitHub Action workflows
- `admin:org` → `manage_runners:org` - Manage organization runners


### runner_script.sh

Helper script for building and managing the runner Docker image.

## Building the Image
Due to repository permissions, GitHub Actions cannot publish to a dedicated E2E runner ECR repository. 
As a workaround, the runner image is published to the existing production ECR repository with a distinct naming convention:

**laa-amend-a-claim-prod:github-e2e-runner-${{ github.run_id }}.**

This ensures unique tagging while maintaining access to the shared registry.

## Running Locally (Testing)

```bash
cd scripts
docker build -f Dockerfile.e2e-runner -t laa-amend-a-claim-e2e-runner:latest .
```

```bash
docker run -d \
  -e GITHUB_TOKEN="your_runner_token" \
  -e GITHUB_REPOSITORY="ministryofjustice/laa-amend-a-claim" \
  --name github-runner \
  laa-amend-a-claim-e2e-runner:latest
```

## Kubernetes Deployment

The runner is deployed via Helm chart located at `.helm/e2e-stack/`.

**Prerequisites:**
1. Create GitHub runner token secret:
```bash
kubectl create secret generic github-runner-token \
  --from-literal=token=YOUR_GITHUB_RUNNER_TOKEN
```

2. Deploy E2E stack (includes runner):
```bash
helm upgrade --install e2e-stack .helm/e2e-stack \
  --set githubRunner.enabled=true \
  --set githubRunner.image.repository=YOUR_REGISTRY/laa-amend-a-claim-e2e-runner \
  --set githubRunner.image.tag=latest
```

## Using in GitHub Workflows

Target the runner by its labels in your workflow:

```yaml
name: E2E Tests

on:
  push:
    branches: [ main ]
  pull_request:

jobs:
  e2e:
    runs-on: [self-hosted, abc-e2e]
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Run E2E Tests
        run: |
          cd src/e2e
          ./gradlew test
```

### Why Self-Hosted Runner in K8s?

1. **Internal Service Access**
   - Runner pod runs in same namespace as data-claims-api and laa-amend-a-claim
   - E2E tests can access services via DNS: `http://e2e-amend-a-claim:8084`
   - No need for ingress, port-forwarding, or external exposure

2. **Ephemeral Test Environment**
   - Each test run uses fresh database (PostgreSQL sidecar with emptyDir)
   - Redis session store with no persistence (clean state per test)
   - Consistent, reproducible test conditions

3. **Security**
   - No external access required for test services
   - Services remain ClusterIP (internal only)
   - Secrets managed via Kubernetes native mechanisms

4. **Resource Efficiency**
   - Browser automation runs close to application
   - Reduced network latency
   - Shared cluster resources (CPU/memory)

## Configuration Details

### Redis Configuration

Redis uses emptyDir volume with explicit `--dir /data` to:
- Provide writable directory for Redis temporary files
- Prevent "MISCONF unable to persist to disk" errors
- Keep data ephemeral (cleared on pod restart)
- Avoid read-only filesystem issues with security contexts

### PostgreSQL Configuration

PostgreSQL sidecar in data-claims-api deployment:
- Runs on port 5435 (non-standard to avoid conflicts)
- Flyway migrations applied on startup
- Test data seeded via e2e tests 

## Troubleshooting

### Runner Not Connecting

Check runner logs:
```bash
kubectl logs deployment/github-runner
```

Common issues:
- Invalid GITHUB_TOKEN (token expired or incorrect)
- GITHUB_REPOSITORY format incorrect (must be `owner/repo`)
- Runner name conflict (delete old runner from GitHub Settings)

### E2E Tests Failing

Check service connectivity from runner:
```bash
kubectl exec -it deployment/github-runner -- bash
curl http://e2e-data-claims-api:8082/actuator/health
curl http://e2e-amend-a-claim:8084/actuator/health
```

### Redis Persistence Errors

If you see "MISCONF" errors, verify:
```bash
kubectl exec -it deployment/e2e-amend-a-claim -c redis -- redis-cli -p 6380 CONFIG GET dir
kubectl exec -it deployment/e2e-amend-a-claim -c redis -- redis-cli -p 6380 CONFIG GET save
```

Should show:
- `dir = /data`
- `save = ""` (empty = no RDB snapshots)

## Maintenance

### Updating Runner Version

1. Update `RUNNER_VERSION` in Dockerfile.e2e-runner
2. Rebuild image
3. Update Helm chart image tag
4. Redeploy

### Cleaning Up Old Runners

Runners auto-remove on graceful shutdown, but if pod crashes:
```bash
# Via GitHub UI: Settings > Actions > Runners > Remove
# Or via API/CLI if automated cleanup needed
```


