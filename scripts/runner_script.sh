#!/bin/bash
set -e

# Validate required environment variables
if [ -z "${GITHUB_TOKEN}" ]; then
    echo "ERROR: GITHUB_TOKEN environment variable is required"
    exit 1
fi

if [ -z "${GITHUB_REPOSITORY}" ]; then
    echo "ERROR: GITHUB_REPOSITORY environment variable is required"
    exit 1
fi

# Fetch runner registration token
echo "Fetching runner registration token..."
RUNNER_TOKEN=$(curl -sL \
    -X POST \
    -H "Accept: application/vnd.github+json" \
    -H "Authorization: Bearer ${GITHUB_TOKEN}" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    "https://api.github.com/repos/${GITHUB_REPOSITORY}/actions/runners/registration-token" \
    | jq -r .token)

if [ -z "${RUNNER_TOKEN}" ] || [ "${RUNNER_TOKEN}" = "null" ]; then
    echo "ERROR: Failed to get runner registration token"
    exit 1
fi

RUNNER_NAME=${RUNNER_NAME:-"k8s-e2e-runner"}

echo "Configuring GitHub Actions runner..."
echo "URL: https://github.com/${GITHUB_REPOSITORY}"
echo "Runner name: ${RUNNER_NAME}"

# Remove existing runner configuration if it exists
if [ -f ".runner" ]; then
    echo "Found existing runner configuration, removing..."
    ./config.sh remove --token "${RUNNER_TOKEN}" 2>/dev/null || true
fi

# Configure the runner
./config.sh \
    --url "https://github.com/${GITHUB_REPOSITORY}" \
    --token "${RUNNER_TOKEN}" \
    --name "${RUNNER_NAME}" \
    --labels "${RUNNER_LABELS:-self-hosted,linux,e2e}" \
    --unattended \
    --replace

cleanup() {
    echo "Removing runner..."
    if [ -f ".runner" ]; then
        # Get a new token for removal
        REMOVE_TOKEN=$(curl -sL \
            -X POST \
            -H "Accept: application/vnd.github+json" \
            -H "Authorization: Bearer ${GITHUB_TOKEN}" \
            -H "X-GitHub-Api-Version: 2022-11-28" \
            "https://api.github.com/repos/${GITHUB_REPOSITORY}/actions/runners/registration-token" \
            | jq -r .token)
        if [ -n "${REMOVE_TOKEN}" ] && [ "${REMOVE_TOKEN}" != "null" ]; then
            ./config.sh remove --token "${REMOVE_TOKEN}"
        fi
    fi
}

trap cleanup EXIT
trap cleanup SIGINT SIGTERM

./run.sh