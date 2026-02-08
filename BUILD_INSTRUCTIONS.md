# Instruksi Build Aplikasi Catatanku

## Kredensial Keystore (SIMPAN INI!)

File keystore: `keystore.jks` (sudah ada di root project)

- **Keystore Password**: `Catatanku2025`
- **Key Alias**: `catatanku`
- **Key Password**: `Catatanku2025`

> ⚠️ PENTING: Jaga keystore dan password ini dengan baik! Jika hilang, Anda tidak bisa update aplikasi di Play Store.

---

## Cara Install Android SDK

### Option 1: Install Android Studio (Recommended)

1. Download Android Studio dari: https://developer.android.com/studio
2. Install seperti biasa
3. Buka Android Studio dan go through setup wizard
4. SDK akan terinstall otomatis ke:
   - Windows: `C:\Users\[User]\AppData\Local\Android\Sdk`

### Option 2: Command Line SDK Only

Jika tidak ingin install Android Studio penuh:

```bash
# Download command line tools
# Windows: https://developer.android.com/studio#command-tools
# Extract ke: C:\android-sdk

# Set environment variables:
setx ANDROID_HOME "C:\Users\[User]\AppData\Local\Android\Sdk"
setx ANDROID_SDK_ROOT "%ANDROID_HOME%"
setx PATH "%PATH%;%ANDROID_HOME%\cmdline-tools\latest\bin;%ANDROID_HOME%\platform-tools"
```

---

## Cara Build APK dan AAB

Setelah Android SDK terinstall:

### 1. Masuk ke project directory
```bash
cd "D:\Dari Desktop\Claude\Android\notesimpel"
```

### 2. Accept SDK licenses (pertama kali)
```bash
sdkmanager --licenses
# Accept semua dengan mengetik 'y'
```

### 3. Build APK Release
```bash
.\gradlew assembleRelease
```

Output: `app\build\outputs\apk\release\app-release.apk`

### 4. Build AAB (untuk Play Store)
```bash
.\gradlew bundleRelease
```

Output: `app\build\outputs\bundle\release\app-release.aab`

---

## Struktur Project
```
notesimpel/
├── app/
│   └── src/main/
│       ├── java/com/daricreative/catatanku/
│       │   ├── MainActivity.kt
│       │   ├── NoteEntity.kt
│       │   ├── NoteDao.kt
│       │   ├── AppDatabase.kt
│       │   └── NoteAdapter.kt
│       ├── res/
│       │   ├── layout/
│       │   ├── values/
│       │   └── mipmap-xxx/ (icon)
│       └── AndroidManifest.xml
├── keystore.jks
├── build.gradle.kts
└── gradlew.bat
```

---

## Info Aplikasi

- **Nama**: Catatanku
- **Package**: com.daricreative.catatanku
- **Versi**: 1.0.0 (versionCode: 1)
- **Min SDK**: 24 (Android 7.0+)
- **Target SDK**: 34 (Android 14)

---

## Troubleshooting

### Jika muncul error "SDK location not found"
Buat file `local.properties` di root project:
```properties
sdk.dir=C\:\\Users\\[YourUsername]\\AppData\\Local\\Android\\Sdk
```

### Jika gradlew tidak bisa jalan
```bash
# Pastikan file punya permission
chmod +x gradlew  # Linux/Mac
# Di Windows biasanya sudah ok
```

### Jika build gagal
```bash
# Clean build
.\gradlew clean
.\gradlew build --info
```
