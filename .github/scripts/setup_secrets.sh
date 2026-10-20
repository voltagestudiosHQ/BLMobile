#!/bin/bash

# GitHub Actions Secrets Setup Script
# This script helps you set up the required secrets for building BedroomLauncher Mobile

set -e

echo "=========================================="
echo "BedroomLauncher Mobile - Secrets Setup"
echo "=========================================="
echo ""

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    echo "ERROR: GitHub CLI (gh) is not installed."
    echo "Install it from: https://cli.github.com/"
    exit 1
fi

# Check authentication
if ! gh auth status &> /dev/null; then
    echo "ERROR: Not authenticated with GitHub CLI."
    echo "Run: gh auth login"
    exit 1
fi

echo "✓ GitHub CLI is installed and authenticated"
echo ""

# Get repository info
REPO_OWNER="$(gh repo view --json owner --jq '.owner.login' 2>/dev/null || echo "")"
REPO_NAME="$(gh repo view --json name --jq '.name' 2>/dev/null || echo "")"

if [ -z "$REPO_OWNER" ] || [ -z "$REPO_NAME" ]; then
    echo "WARNING: Could not determine repository. Please specify manually."
    read -p "Repository owner: " REPO_OWNER
    read -p "Repository name: " REPO_NAME
fi

echo "Repository: $REPO_OWNER/$REPO_NAME"
echo ""

# Function to set a secret
set_secret() {
    local name="$1"
    local value="$2"
    echo -n "Setting secret '$name'... "
    if printf '%s' "$value" | gh secret set "$name" --repo "$REPO_OWNER/$REPO_NAME" > /dev/null 2>&1; then
        echo "✓"
    else
        echo "✗ (already exists or error)"
    fi
}

# Function to prompt for a secret
set_secret_prompt() {
    local name="$1"
    local prompt="$2"
    echo -n "Setting secret '$name'... "
    read -s -p "$prompt: " value
    echo ""
    if printf '%s' "$value" | gh secret set "$name" --repo "$REPO_OWNER/$REPO_NAME" > /dev/null 2>&1; then
        echo "✓"
    else
        echo "✗ (already exists or error)"
    fi
}

echo "=========================================="
echo "Keystore Setup"
echo "=========================================="
echo ""

# Check if keystore exists
if [ -f "voltage_studios.jks" ]; then
    echo "✓ Found voltage_studios.jks"
else
    echo "⚠ Keystore voltage_studios.jks not found in current directory"
    echo "  Run this script from the repository root"
fi

echo ""
echo "=========================================="
echo "Setting up GitHub Secrets"
echo "=========================================="
echo ""

# For signing secrets, generate or use existing
if [ -f "voltage_studios.jks" ]; then
    echo "Using existing voltage_studios.jks keystore"
    echo ""
    
    # Read password from .store_password.txt if exists
    if [ -f ".store_password.txt" ]; then
        STORE_PASSWORD=$(cat .store_password.txt)
        KEY_PASSWORD=$(cat .key_password.txt 2>/dev/null || echo "$STORE_PASSWORD")
        set_secret "STORE_PASSWORD" "$STORE_PASSWORD"
        set_secret "KEY_PASSWORD" "$KEY_PASSWORD"
    else
        echo "Please enter the keystore password:"
        set_secret_prompt "STORE_PASSWORD" "Keystore password"
        set_secret_prompt "KEY_PASSWORD" "Key password (same as store password if unsure)"
    fi
else
    echo "Generating new signing secrets..."
    # Generate a secure random password
    STORE_PASSWORD=$(cat /dev/urandom | head -c 24 | base64)
    echo "  Generated password (save this!): $STORE_PASSWORD"
    set_secret "STORE_PASSWORD" "$STORE_PASSWORD"
    set_secret "KEY_PASSWORD" "$STORE_PASSWORD"
fi

echo ""
echo "=========================================="
echo "API Keys Setup"
echo "=========================================="
echo ""

set_secret_prompt "OAUTH_CLIENT_ID" "Microsoft OAuth Client ID"

echo ""
set_secret_prompt "CURSEFORGE_API_KEY" "CurseForge API Key (optional, press Enter to skip)"

echo ""
echo "=========================================="
echo "✓ Secrets Setup Complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Verify secrets at: https://github.com/$REPO_OWNER/$REPO_NAME/settings/secrets/actions"
echo "2. Update build.gradle.kts to use voltage_studios.jks if needed"
echo "3. Run workflow to test signing"
echo ""
echo "Note: Store the passwords securely. They cannot be retrieved once set."
