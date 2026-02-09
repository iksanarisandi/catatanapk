# Android Builder - Quick Commands Reference

## Generate Keystore

```bash
keytool -genkeypair \
  -v \
  -storetype JKS \
  -keystore app-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -sigalg SHA256withRSA \
  -validity 10000 \
  -alias appkey \
  -storepass "YourSecurePassword123!" \
  -keypass "YourSecurePassword123!" \
  -dname "CN=AppName, O=Company, L=City, ST=Province, C=ID"
```

## Encode Keystore for GitHub Secrets

```bash
base64 -w 0 app-release.jks | gh secret set KEYSTORE_BASE64
```

## Set GitHub Secrets

```bash
echo "YourPassword" | gh secret set KEYSTORE_PASSWORD
echo "appkey" | gh secret set KEY_ALIAS
echo "YourPassword" | gh secret set KEY_PASSWORD
```

## Build Commands

```bash
# Build APK (for direct installation)
./gradlew assembleRelease

# Build AAB (for Play Store)
./gradlew bundleRelease

# Clean and build
./gradlew clean assembleRelease
```

## Output Locations

- **APK**: `app/build/outputs/apk/release/app-release.apk`
- **AAB**: `app/build/outputs/bundle/release/app-release.aab`

## Verify Signature

```bash
# Verify APK
keytool -printcert -jarfile app-release.apk

# Verify AAB
keytool -printcert -jarfile app-release.aab
```

## Install APK on Device

```bash
# Via ADB
adb install app/build/outputs/apk/release/app-release.apk

# Force reinstall
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Common Issues

### "Tag number over 30" Error
Cause: Wrong keystore algorithm (SHA384withRSA)
Fix: Regenerate with SHA256withRSA

### "SDK location not found"
Create `local.properties`:
```properties
sdk.dir=C\:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk
```

### "Keystore file not found" in CI/CD
Check:
1. KEYSTORE_BASE64 secret is set
2. Keystore path in build.gradle.kts matches decoded location
3. All 4 env vars are set: KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD

### APK marked as dangerous
Ensure:
1. `isMinifyEnabled = true` in release build
2. ProGuard rules are complete
3. APK is signed (not debug build)
