# Best Practice: Membuat Project Android Signed APK & AAB

Dokumentasi ini berisi best practice dan pelajaran dari pengalaman development aplikasi Android **Catatanku** untuk menghindari kendala yang sama di kemudian hari.

---

## 📋 Daftar Isi

1. [Project Setup](#1-project-setup)
2. [Keystore & Signing](#2-keystore--signing)
3. [GitHub Actions](#3-github-actions)
4. [Konfigurasi Build](#4-konfigurasi-build)
5. [Dependencies](#5-dependencies)
6. [Common Pitfalls](#6-common-pitfalls)
7. [Troubleshooting](#7-troubleshooting)
8. [Checklist](#8-checklist)

---

## 1. Project Setup

### 1.1 Package Name

❌ **Hindari package name umum:**
```kotlin
// BENTROK dengan aplikasi lain!
com.example.app
com.catatanku.app
```

✅ **Gunakan package name unik:**
```kotlin
com.namaanda.aplikasi
com.perusahaan.produk
com.daricreative.catatanku
```

### 1.2 Struktur Project Minimum

```
project/
├── app/
│   ├── src/main/
│   │   ├── java/com/package/app/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── mipmap-anydpi-v26/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
├── .github/workflows/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

### 1.3 SDK Version

```kotlin
// Minimum yang direkomendasikan
minSdk = 24  // Android 7.0+ (covers 95%+ devices)

// Target latest stable
targetSdk = 34  // Android 14

compileSdk = 34
```

---

## 2. Keystore & Signing

### 2.1 Generate Keystore yang Benar

❌ **JANGAN gunakan algoritma yang tidak didukung:**
```bash
# SHA384withRSA menyebabkan "Tag number over 30" error!
keytool -genkeypair -sigalg SHA384withRSA ...
```

✅ **Gunakan format yang kompatibel:**
```bash
keytool -genkeypair \
  -v \
  -storetype JKS \
  -keystore release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -sigalg SHA256withRSA \
  -validity 10000 \
  -alias appkey \
  -storepass "PasswordKuat!" \
  -keypass "PasswordKuat!" \
  -dname "CN=NamaApp, O=Perusahaan, L=Kota, ST=Provinsi, C=ID"
```

### 2.2 Simpan Keystore dengan Aman

🔐 **PENTING:** Keystore TIDAK BISA diganti jika hilang!

```bash
# Backup keystore di beberapa lokasi
cp release.jks ~/backup/
cp release.jks ~/google-drive/
cp release.jks ~/dropbox/

# JANGAN commit keystore ke repo Git!
echo "*.jks" >> .gitignore
echo "release.jks" >> .gitignore
```

### 2.3 Credential Management

Gunakan password yang kuat dan simpan di password manager:

```bash
# ✅ Good password
MyApp@2025!Secure

# ❌ Bad password
123456
password
app123
```

---

## 3. GitHub Actions

### 3.1 Workflow untuk Signed Build

Key lessons dari troubleshooting:

1. **Keystore Path Resolution**
   - Decode keystore ke root directory
   - Gunakan `project.rootProject.file()` di build.gradle.kts

2. **Environment Variables**
   - Simpan password di GitHub Secrets, bukan hardcode
   - Gunakan `${{ secrets.SECRET_NAME }}` untuk mengakses

3. **Working Directory**
   - `file(keystorePath)` → relatif terhadap module
   - `project.rootProject.file(keystorePath)` → relatif terhadap root

### 3.2 Workflow Template

```yaml
name: Build Android APK & AAB

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - uses: android-actions/setup-android@v3

      - name: Decode Keystore
        # Decode keystore ke root directory
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > release-keystore.jks

      - name: Build Release
        env:
          # Path relatif terhadap root project
          KEYSTORE_PATH: release-keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew assembleRelease bundleRelease

      - uses: actions/upload-artifact@v4
        with:
          name: app-release
          path: app/build/outputs/
```

### 3.3 Setup GitHub Secrets

```bash
# Setup keystore base64
base64 -w 0 release.jks | gh secret set KEYSTORE_BASE64

# Setup passwords
echo "YourPassword" | gh secret set KEYSTORE_PASSWORD
echo "YourAlias" | gh secret set KEY_ALIAS
echo "YourPassword" | gh secret set KEY_PASSWORD
```

---

## 4. Konfigurasi Build

### 4.1 Signing Config yang Benar

❌ **JANGAN hardcode credentials:**
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../release.jks")
        storePassword = "Password123"  // ❌ SECURITY RISK!
        keyAlias = "key"
        keyPassword = "Password123"
    }
}
```

✅ **Gunakan environment variables:**
```kotlin
signingConfigs {
    create("release") {
        val keystorePath = System.getenv("KEYSTORE_PATH")
        val storePass = System.getenv("KEYSTORE_PASSWORD")
        val keyAlias = System.getenv("KEY_ALIAS")
        val keyPass = System.getenv("KEY_PASSWORD")

        if (keystorePath != null && storePass != null && keyAlias != null && keyPass != null) {
            storeFile = project.rootProject.file(keystorePath)
            storePassword = storePass
            this.keyAlias = keyAlias
            keyPassword = keyPass
        }
    }
}
```

### 4.2 Disable Minify untuk Development

```kotlin
buildTypes {
    release {
        // Untuk development, disable minify
        // Enable hanya jika proguard rules sudah lengkap
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### 4.3 Disable Minify Saat Troubleshooting

Jika build gagal dan Anda tidak yakin apakah masalahnya dari code atau ProGuard:

```kotlin
buildTypes {
    release {
        isMinifyEnabled = false  // Disable dulu
    }
}
```

---

## 5. Dependencies

### 5.1 Dependencies Stables (Recommended)

```kotlin
dependencies {
    // Core - selalu gunakan versi stabil terbaru
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 5.2 KSP for Room

```kotlin
// build.gradle.kts (project level)
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}

// app/build.gradle.kts
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    ksp("androidx.room:room-compiler:$roomVersion")
}
```

### 5.3 ExportSchema Room

❌ **Warning saat build:**
```
Schema export directory was not provided
```

✅ **Solusi 1 - Disable schema export:**
```kotlin
@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
```

✅ **Solusi 2 - Provide schema location:**
```kotlin
// In build.gradle.kts
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

---

## 6. Common Pitfalls

### 6.1 Vector Drawable Issues

❌ **JANGAN gunakan <text> element dalam vector drawable:**
```xml
<!-- AAPT error! -->
<vector>
    <text
        android:text="Note"
        android:textSize="32" />
</vector>
```

✅ **Gunakan path untuk icon:**
```xml
<vector>
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M6,2 L18,2 C19.1,2 20,2.9 20,4 ..."/>
</vector>
```

### 6.2 Duplicate Resources

❌ **JANGAN define resource yang sama dua kali:**
```xml
<!-- colors.xml -->
<color name="primary">#4FC3F7</color>

<!-- colors_launcher.xml -->
<color name="primary">#4FC3F7</color>  <!-- ❌ Duplicate! -->
```

✅ **Gabung ke satu file:**
```xml
<!-- colors.xml -->
<color name="primary">#4FC3F7</color>
<color name="ic_launcher_background">#4FC3F7</color>
```

### 6.3 Missing Imports

Selalu periksa imports saat error "Unresolved reference":

```kotlin
// ❌ Lupa import
class ItemNoteBinding(private val rootView: View) {
    fun inflate(inflater: LayoutInflater, ...): ItemNoteBinding {
        // Error: Unresolved reference: LayoutInflater
    }
}

// ✅ Import lengkap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ItemNoteBinding(private val rootView: View) {
    fun inflate(inflater: LayoutInflater, ...): ItemNoteBinding {
        // Works!
    }
}
```

### 6.4 Gradle Wrapper

Selalu include Gradle wrapper di repo:

```bash
# ✅ Include gradle wrapper
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
gradlew
gradlew.bat

# ❌ Don't ignore
!gradle/wrapper/
```

---

## 7. Troubleshooting

### 7.1 Build Gagal di GitHub Actions

**Masalah:** "Keystore file not found"

**Diagnosis:**
```bash
# Cek log workflow
gh run view [run-id] --log | grep "Keystore file"
```

**Solusi:**
1. Pastikan keystore didecode ke lokasi yang benar
2. Gunakan `project.rootProject.file()` untuk path dari root
3. Pastikan semua 4 env vars ter-set dengan benar

### 7.2 Tag Number Over 30 Error

**Masalah:** `Tag number over 30 is not supported`

**Penyebab:** Algoritma signature tidak kompatibel

**Solusi:**
```bash
# Gunakan SHA256withRSA, bukan SHA384withRSA
keytool -genkeypair -sigalg SHA256withRSA ...
```

### 7.3 Duplicate Resource Error

**Masalah:** `Resource and asset merger: Duplicate resources`

**Solusi:**
```bash
# Cari duplicate resources
grep -r "name=\"primary\"" app/src/main/res/values/

# Gabung atau hapus yang duplikat
```

### 7.4 Kotlin Compilation Error

**Masalah:** `Unresolved reference`

**Cek:**
```bash
# Cek file yang error
app/src/main/java/com/package/SomeFile.kt:16:31

# Tambahkan import yang hilang
```

---

## 8. Checklist

### 8.1 Sebelum Mulai Project

- [ ] Pilih package name yang unik
- [ ] Tentukan minSdk dan targetSdk yang sesuai
- [ ] Generate keystore dengan format yang kompatibel
- [ ] Simpan keystore dan password di tempat aman
- [ ] Setup .gitignore dengan benar

### 8.2 Setup GitHub Actions

- [ ] Generate dan encode keystore ke base64
- [ ] Setup GitHub Secrets:
  - [ ] `KEYSTORE_BASE64`
  - [ ] `KEYSTORE_PASSWORD`
  - [ ] `KEY_ALIAS`
  - [ ] `KEY_PASSWORD`
- [ ] Test workflow dengan commit dummy
- [ ] Verify artifacts ter-upload dengan benar

### 8.3 Sebelum Release

- [ ] Update versionCode dan versionName
- [ ] Test di device fisik (bukan hanya emulator)
- [ ] Cek bahwa signing config menggunakan correct keystore
- [ ] Verify APK bisa di-install
- [ ] Verify AAB bisa di-upload ke Play Console

### 8.4 Backup

- [ ] Backup keystore file
- [ ] Backup password di password manager
- [ ] Backup project ke GitHub/GitLab
- [ ] Dokumentasikan semua credential

---

## 9. Resource Generator Tools

### 9.1 Icon Generator

Untuk membuat icon launcher dengan mudah:

- **Android Asset Studio:** https://romannurik.github.io/AndroidAssetStudio/

### 9.2 APK Signing Verification

```bash
# Verify APK signature
keytool -printcert -jarfile app-release.apk

# Verify AAB signature
keytool -printcert -jarfile app-release.aab
```

---

## 10. Quick Reference

### Generate Keystore Command
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
  -storepass "SecurePass@2025!" \
  -keypass "SecurePass@2025!" \
  -dname "CN=AppName, O=Company, L=City, ST=Province, C=ID"
```

### Encode Keystore for GitHub
```bash
base64 -w 0 app-release.jks | gh secret set KEYSTORE_BASE64
```

### Verify Build Locally
```bash
# Build APK
./gradlew assembleRelease

# Build AAB
./gradlew bundleRelease

# Output:
# APK: app/build/outputs/apk/release/app-release.apk
# AAB: app/build/outputs/bundle/release/app-release.aab
```

---

## 11. Best Practice Summary

| Area | Best Practice | Why |
|------|---------------|-----|
| Package Name | Unik & spesifik | Hindari bentrok dengan app lain |
| Keystore | SHA256withRSA, JKS format | Kompatibel dengan semua Android build tools |
| Password | Kuat & unik | Security |
| Storage | JANGAN commit ke repo | Risk kebocoran |
| Backup | Multiple locations | Jangan hilang! |
| Signing Config | Environment variables | Bisa beda antara local dan CI/CD |
| Minify | False untuk development | Hindari error ProGuard |
| Dependencies | Versi stabil | Hindari experimental/beta |
| Build Config | project.rootProject.file() | Path resolution yang benar |

---

**Document Version:** 1.0
**Last Updated:** 2026-02-08
**Based On:** Project Catatanku Development Experience
