---
name: android-builder
description: Use this skill when the user asks to "create android app", "build android project", "make android application", "start android development", mentions "android app" with create/make/build, or discusses Android project setup. Automatically applies best practices for Android development including proper package naming, keystore setup, GitHub Actions CI/CD, and ProGuard configuration.
version: 1.0.0
---

# Android Builder Skill

This skill provides a complete, production-ready template for creating Android applications with Kotlin. Use this when the user wants to create a new Android project or app.

## When This Skill Activates

This skill activates when the user:
- Asks to create, build, or make an Android app/project
- Mentions "android app" with creation context
- Wants to start Android development
- Needs Android project setup guidance

## Core Principles

1. **Security First**: Never commit keystore files, use environment variables for signing
2. **Modern Android**: Kotlin, Material Design 3, Room Database, Coroutines
3. **CI/CD Ready**: GitHub Actions for automated builds
4. **Code Obfuscation**: Enable ProGuard/R8 for release builds to prevent false positives
5. **Clean Architecture**: Separation of concerns, testable code

## Project Structure Template

```
project-name/
├── app/
│   ├── src/main/
│   │   ├── java/com/package/appname/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── entities/
│   │   │   │   ├── dao/
│   │   │   │   └── database/
│   │   │   ├── ui/
│   │   │   │   └── adapters/
│   │   │   └── utils/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── drawable/
│   │   │   └── mipmap-*/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── .github/workflows/
│   └── build.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

## Required Configuration

### 1. Package Naming

Always use unique package names to avoid conflicts:

```
❌ com.example.app
❌ com.myapp.app

✅ com.username.appname
✅ com.company.product
✅ com.yourname.creativity
```

### 2. SDK Versions

```kotlin
minSdk = 24       // Android 7.0+ (covers 95%+ devices)
targetSdk = 35    // Android 15
compileSdk = 35
```

### 3. Build Configuration (app/build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.package.appname"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.package.appname"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            // IMPORTANT: Enable minification to prevent antivirus false positives!
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Signing config with environment variables
    val keystorePath = System.getenv("KEYSTORE_PATH")
    val storePass = System.getenv("KEYSTORE_PASSWORD")
    val keyAlias = System.getenv("KEY_ALIAS")
    val keyPass = System.getenv("KEY_PASSWORD")

    if (keystorePath != null && storePass != null && keyAlias != null && keyPass != null) {
        signingConfigs {
            create("release") {
                storeFile = project.rootProject.file(keystorePath)
                storePassword = storePass
                this.keyAlias = keyAlias
                keyPassword = keyPass
            }
        }
        buildTypes.named("release").configure {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 4. ProGuard Rules (app/proguard-rules.pro)

```proguard
# Keep Room entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Kotlin reflection
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Keep View constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
```

### 5. GitHub Actions Workflow (.github/workflows/build.yml)

```yaml
name: Build Android APK & AAB

on:
  push:
    branches: [ main, master ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Android SDK
        uses: android-actions/setup-android@v3

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Decode Keystore
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > release-keystore.jks

      - name: Build Release
        env:
          KEYSTORE_PATH: release-keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew assembleRelease bundleRelease --stacktrace

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-release-apk
          path: app/build/outputs/apk/release/app-release.apk
          retention-days: 90

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: app-release-aab
          path: app/build/outputs/bundle/release/app-release.aab
          retention-days: 90
```

### 6. .gitignore

```gitignore
# Built application files
*.apk
*.ap_
*.aab

# Java class files
*.class

# Generated files
bin/
gen/
out/
release/

# Gradle files
.gradle/
build/

# Local configuration file
local.properties

# Log Files
*.log

# Keystore files - CRITICAL!
*.jks
*.keystore

# Android Studio
.idea/
*.iml

# macOS
.DS_Store
```

## Keystore Setup

### Generate Keystore (CRITICAL - Use Correct Algorithm!)

```bash
# CORRECT - SHA256withRSA
keytool -genkeypair \
  -v \
  -storetype JKS \
  -keystore app-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -sigalg SHA256withRSA \
  -validity 10000 \
  -alias appkey \
  -storepass "SecurePass@2025!" \
  -keypass "SecurePass@2025!" \
  -dname "CN=AppName, O=Company, L=City, ST=Province, C=ID"

# WRONG - Causes "Tag number over 30" error!
# keytool -genkeypair -sigalg SHA384withRSA ...
```

### Setup GitHub Secrets

```bash
# Encode keystore to base64
base64 -w 0 app-release.jks | gh secret set KEYSTORE_BASE64

# Set passwords
echo "YourPassword" | gh secret set KEYSTORE_PASSWORD
echo "appkey" | gh secret set KEY_ALIAS
echo "YourPassword" | gh secret set KEY_PASSWORD
```

## Common Pitfalls to Avoid

### 1. Vector Drawable Text Elements
❌ DON'T use `<text>` in vector drawables - causes AAPT error
✅ Use `<path>` elements for icons

### 2. Duplicate Resources
❌ DON'T define same resource name in multiple XML files
✅ Combine all resources in single files (colors.xml, strings.xml)

### 3. Missing Imports
Always check imports when "Unresolved reference" error occurs

### 4. Keystore in Git
❌ NEVER commit .jks files to repository
✅ Add to .gitignore and use environment variables

### 5. Disabled Minification
❌ DON'T release with `isMinifyEnabled = false` - may trigger antivirus
✅ Always enable for release builds

## Development Workflow

1. **Initialize Project**
   - Create project structure
   - Configure build.gradle.kts
   - Setup .gitignore

2. **Generate Keystore**
   - Create keystore with correct algorithm
   - Store credentials securely
   - Setup GitHub Secrets

3. **Implement Features**
   - Use Room for local storage
   - Use Coroutines for async operations
   - Follow Material Design guidelines

4. **Setup CI/CD**
   - Create GitHub Actions workflow
   - Test build locally first
   - Verify artifacts upload correctly

5. **Release**
   - Update versionCode and versionName
   - Test on physical device
   - Verify APK installs and AAB uploads to Play Console

## Quick Start Commands

```bash
# Create new project (use Android Studio or command line)
# Then apply this skill's template

# Generate keystore
keytool -genkeypair -v -storetype JKS -keystore app-release.jks \
  -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 10000 \
  -alias appkey -storepass "SecurePass@2025!" -keypass "SecurePass@2025!"

# Setup GitHub secrets
base64 -w 0 app-release.jks | gh secret set KEYSTORE_BASE64

# Build locally
./gradlew assembleRelease    # APK
./gradlew bundleRelease      # AAB

# Output locations
# APK: app/build/outputs/apk/release/app-release.apk
# AAB: app/build/outputs/bundle/release/app-release.aab
```

## References

For additional details, see:
- Android Best Practices: https://developer.android.com/quality
- Material Design: https://m3.material.io/
- Room Database: https://developer.android.com/training/data-storage/room
