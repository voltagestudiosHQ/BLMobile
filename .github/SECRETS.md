# GitHub Actions Secrets

This document lists all secrets required for GitHub Actions workflows.

> **⚠️ IMPORTANT**: Never commit actual secret values to version control. This file contains only the secret names and descriptions, not the actual values.

## Required Secrets

### Signing Secrets

| Secret Name | Description | Notes |
|------------|-------------|-------|
| `STORE_PASSWORD` | Release keystore password | Generated: `cat /dev/urandom | head -c 24 | base64` |
| `KEY_PASSWORD` | Key password for `voltage!studios` alias | Same as STORE_PASSWORD |

### API Keys

| Secret Name | Description | Notes |
|------------|-------------|-------|
| `OAUTH_CLIENT_ID` | Microsoft OAuth Client ID | From Azure Portal |
| `CURSEFORGE_API_KEY` | CurseForge API Key | Optional, from CurseForge |

### Default Fallback (from gradle.properties)

The build system has default passwords in `BedroomLauncherMobile/gradle.properties`:
- `default_store_password`
- `default_key_password`

These are used as fallbacks if the corresponding secrets are not set.

## How to Add Secrets

### Method 1: GitHub Web UI (Recommended)

1. Go to your repository on GitHub: `https://github.com/<owner>/<repo>/settings/secrets/actions`
2. Click **New repository secret**
3. Enter the **Name** from the table above
4. Enter the **Value** (see below for where to get each value)
5. Click **Add secret**
6. Repeat for each secret

#### Secret Values to Use:

**STORE_PASSWORD & KEY_PASSWORD**:
```bash
# Generate a new random password (recommended for each environment)
cat /dev/urandom | head -c 24 | base64
# Or use the one generated for voltage_studios.jks: 4Pgq+RwCIn2zS1HhNMrN2hnzzV4hGg7s
```

**OAUTH_CLIENT_ID**:
- Create at: [Azure Portal](https://portal.azure.com/)
- App Registration → New Registration
- Redirect URI: `com.movtery.zalithlauncher:/oauth2redirect`

**CURSEFORGE_API_KEY**:
- Get at: [CurseForge Console](https://console.curseforge.com/)
- Register for API access

### Method 2: GitHub CLI

```bash
# Install GitHub CLI: https://cli.github.com/

# Authenticate
gh auth login

# Set secrets for this repository (replace <owner>/<repo>)
# You will be prompted to enter each value

gh secret set STORE_PASSWORD --repo <owner>/<repo>
# Enter: 4Pgq+RwCIn2zS1HhNMrN2hnzzV4hGg7s (or your generated password)

gh secret set KEY_PASSWORD --repo <owner>/<repo>
# Enter: 4Pgq+RwCIn2zS1HhNMrN2hnzzV4hGg7s (or your generated password)

gh secret set OAUTH_CLIENT_ID --repo <owner>/<repo>
# Enter: Your Microsoft OAuth Client ID

gh secret set CURSEFORGE_API_KEY --repo <owner>/<repo>
# Enter: Your CurseForge API Key
```

### Method 3: GitHub API (Advanced)

```bash
# Replace with your GitHub token and repo details
GITHUB_TOKEN="your_personal_access_token"
REPO_OWNER="<your-org>"
REPO_NAME="<your-repo>"

# Get repository public key
KEY_ID=$(curl -s \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/actions/secrets/public-key | jq -r '.key_id')

PUBLIC_KEY=$(curl -s \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/actions/secrets/public-key | jq -r '.key')

# Encrypt and create STORE_PASSWORD
ENCRYPTED=$(echo -n "4Pgq+RwCIn2zS1HhNMrN2hnzzV4hGg7s" | \
  openssl enc -a -A -base64 -salt -pass pass:$PUBLIC_KEY | base64 -w 0)

curl -X PUT \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/actions/secrets/STORE_PASSWORD \
  -d "{\"encrypted_value\":\"$ENCRYPTED\",\"key_id\":\"$KEY_ID\"}"

# Repeat for KEY_PASSWORD with same value
```

## Keystore Information

### New Keystore: voltage_studios.jks

Created for `voltage!studios` signing:
- **File**: `voltage_studios.jks` (in repository root)
- **Alias**: `voltage!studios`
- **Algorithm**: RSA 2048-bit
- **Validity**: 10,000 days (until March 7, 2054)
- **DN**: CN=voltage!studios, OU=BedroomLauncher, O=voltageStudios, L=Unknown, ST=Unknown, C=US

To use this keystore, update `BedroomLauncherMobile/build.gradle.kts`:
```kotlin
signingConfigs {
    create("releaseBuild") {
        storeFile = file("voltage_studios.jks")  // Change from zalith_launcher.jks
        storePassword = getKeyFromLocal("STORE_PASSWORD", ".store_password.txt")
        keyAlias = "voltage!studios"  // Change from movtery_zalith
        keyPassword = getKeyFromLocal("KEY_PASSWORD", ".key_password.txt")
    }
}
```

### Existing Keystore: zalith_launcher.jks

- **File**: `BedroomLauncherMobile/zalith_launcher.jks`
- **Key Alias**: `movtery_zalith`
- **Default passwords**: Defined in `gradle.properties` (for fallback only)

## Workflow Usage

The `.github/workflows/build.yml` workflow uses these secrets:

```yaml
env:
  KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
  STORE_PASSWORD: ${{ secrets.STORE_PASSWORD }}
  OAUTH_CLIENT_ID: ${{ secrets.OAUTH_CLIENT_ID }}
  CURSEFORGE_API_KEY: ${{ secrets.CURSEFORGE_API_KEY }}
```

These environment variables override the values in `gradle.properties`.

## Security Best Practices

1. **Never commit secrets to version control** - Use GitHub Secrets
2. **Use minimal required permissions** for each secret
3. **Rotate secrets periodically** - Especially after security incidents
4. **Audit secret access** regularly
5. **Restrict repository access** to trusted collaborators only
6. **Use different passwords for different environments** (dev, staging, prod)

## Secret Reference Table

| Secret | Used In | Purpose | Required |
|--------|---------|---------|----------|
| `STORE_PASSWORD` | build.yml | Sign release APKs | ✅ Yes |
| `KEY_PASSWORD` | build.yml | Sign release APKs | ✅ Yes |
| `OAUTH_CLIENT_ID` | build.yml | Microsoft account auth | ✅ Yes |
| `CURSEFORGE_API_KEY` | build.yml | CurseForge downloads | ❌ Optional |

## Troubleshooting

**Build fails with "Keystore file not found"**:
- Ensure `voltage_studios.jks` exists in repository root
- Or update build.gradle to point to correct keystore path

**Signing failed**:
- Verify STORE_PASSWORD and KEY_PASSWORD match the keystore
- Check alias name is correct

**OAuth not working**:
- Verify OAUTH_CLIENT_ID is set correctly
- Check Redirect URI in Azure Portal matches app configuration

**Permission denied**:
- Ensure you have admin access to the repository
- Check repository settings for secret access policies
