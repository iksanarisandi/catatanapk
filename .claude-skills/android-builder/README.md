# Android Builder Skill

Claude Code skill for creating production-ready Android applications with best practices built-in.

## What This Skill Does

When you ask Claude to create an Android app, this skill automatically:
- ✅ Creates proper project structure
- ✅ Configures secure signing (no keystore in git!)
- ✅ Sets up GitHub Actions CI/CD
- ✅ Enables code obfuscation (prevents antivirus false positives)
- ✅ Configures Room Database + Coroutines
- ✅ Applies Material Design 3

## How to Use

Simply ask Claude to create an Android app:

```
"Create a note-taking Android app"
"Build a weather app for Android"
"Make a todo list Android application"
```

Claude will automatically use this skill and apply all best practices.

## What Gets Created

### Project Structure
```
your-app/
├── app/                    # Main app module
├── .github/workflows/      # CI/CD automation
├── build.gradle.kts        # Build configuration
├── proguard-rules.pro      # Code obfuscation rules
└── .gitignore             # Proper excludes (keystore safe!)
```

### Security Features
- Keystore stored in GitHub Secrets (never in repo)
- Code obfuscation enabled for release builds
- Proper signing configuration

### CI/CD Pipeline
- Automated APK & AAB builds
- Signed artifacts
- Easy download from GitHub Actions

## Manual Keystore Setup (If Needed)

```bash
# 1. Generate keystore
keytool -genkeypair \
  -v -storetype JKS \
  -keystore app-release.jks \
  -keyalg RSA -keysize 2048 \
  -sigalg SHA256withRSA \
  -validity 10000 \
  -alias appkey

# 2. Encode for GitHub
base64 -w 0 app-release.jks | gh secret set KEYSTORE_BASE64

# 3. Set other secrets
gh secret set KEYSTORE_PASSWORD
gh secret set KEY_ALIAS
gh secret set KEY_PASSWORD
```

## Troubleshooting

### Build fails with "Keystore not found"
- Check GitHub Secrets are set correctly
- Verify workflow uses correct environment variables

### APK marked as dangerous
- Ensure `isMinifyEnabled = true` in release build
- Check ProGuard rules are complete

### "Tag number over 30" error
- Regenerate keystore with SHA256withRSA (not SHA384!)

## Version

1.0.0 - Initial release

## License

MIT
