# Catatanku - Aplikasi Catatan Sederhana

Aplikasi catatan Android sederhana dengan penyimpanan lokal.

## Fitur

- ✅ Create - Tambah catatan baru
- ✅ Read - Lihat daftar catatan
- ✅ Update - Edit catatan yang sudah ada
- ✅ Delete - Hapus catatan
- 💾 Penyimpanan lokal dengan Room Database
- 🎨 Tema clean dengan warna biru muda

## Info Aplikasi

| Property | Value |
|----------|-------|
| Nama | Catatanku |
| Package | `com.daricreative.catatanku` |
| Versi | 1.0.0 (versionCode: 1) |
| Min SDK | 24 (Android 7.0+) |
| Target SDK | 34 (Android 14) |

## Kredensial Keystore

⚠️ **PENTING**: Simpan informasi ini dengan aman!

```
Keystore File: keystore.jks
Keystore Password: Catatanku2025
Key Alias: catatanku
Key Password: Catatanku2025
```

## Cara Build

Lihat file [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) untuk instruksi lengkap.

### Quick Start

```bash
# 1. Install Android SDK (via Android Studio atau command line)

# 2. Masuk ke project directory
cd "D:\Dari Desktop\Claude\Android\notesimpel"

# 3. Set SDK location (jika belum)
# Copy local.properties.template ke local.properties dan update path SDK

# 4. Build APK Release
.\gradlew assembleRelease

# 5. Build AAB untuk Play Store
.\gradlew bundleRelease
```

## Output Build

- **APK**: `app\build\outputs\apk\release\app-release.apk`
- **AAB**: `app\build\outputs\bundle\release\app-release.aab`

## Generate Icon Online

Untuk membuat icon launcher PNG yang berkualitas:

1. Buka: https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
2. Upload gambar atau teks "Note"
3. Pilih warna background: `#4FC3F7` (Biru Muda)
4. Download dan extract
5. Copy file PNG ke folder mipmap yang sesuai:
   - `mipmap-mdpi/` - 48x48
   - `mipmap-hdpi/` - 72x72
   - `mipmap-xhdpi/` - 96x96
   - `mipmap-xxhdpi/` - 144x144
   - `mipmap-xxxhdpi/` - 192x192

## Struktur Project

```
notesimpel/
├── app/
│   ├── src/main/
│   │   ├── java/com/daricreative/catatanku/
│   │   │   ├── MainActivity.kt          # Activity utama
│   │   │   ├── NoteEntity.kt            # Entity database
│   │   │   ├── NoteDao.kt               # DAO untuk Room
│   │   │   ├── AppDatabase.kt           # Database Room
│   │   │   ├── NoteAdapter.kt           # RecyclerView adapter
│   │   │   └── ItemNoteBinding.kt       # View binding helper
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml    # Layout utama
│   │   │   │   ├── item_note.xml        # Item catatan
│   │   │   │   └── dialog_note.xml      # Dialog tambah/edit
│   │   │   ├── values/
│   │   │   │   ├── strings.xml          # String resources
│   │   │   │   ├── colors.xml           # Color resources
│   │   │   │   └── themes.xml           # Theme
│   │   │   ├── drawable/                # Icon & drawable
│   │   │   └── mipmap-*/                # Launcher icon
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                 # App build config
│   └── proguard-rules.pro               # ProGuard rules
├── keystore.jks                         # Keystore untuk signing
├── build.gradle.kts                     # Root build config
├── settings.gradle.kts                  # Gradle settings
├── gradlew / gradlew.bat                # Gradle wrapper
└── BUILD_INSTRUCTIONS.md                # Instruksi build lengkap
```

## License

Copyright © 2025 DariCreative. All rights reserved.
