#!/bin/bash

# Setup script for Git hooks
echo "Setting up Pre-commit for LAA Amend a Claim..."

# Install prek globally
echo "\nInstalling prek globally"
curl --proto '=https' --tlsv1.2 \
-LsSf https://raw.githubusercontent.com/ministryofjustice/devsecops-hooks/a3f792a077eb216c2e9ac9a4c2eac34cea618ee2/prek/prek-installer.sh | sh

# Activate prek in the repository
echo "\nInstalling prek within the repository"
prek install

echo "Git hooks setup complete!"
echo "The pre-commit hook will now:"
echo "  1. Run Spotless formatting on staged Java files"
echo "  2. Run Checkstyle validation on formatted files"
echo "  3. Run Ministry of Justice - Secrets Scanner"
echo "  4. Run Snyk checks on dependencies"
echo "  5. Run Snyk checks on code"
echo "To manually run Spotless formatting: ./gradlew spotlessApply"
echo "To check Spotless compliance: ./gradlew spotlessCheck"