# Keep Room entities and DAOs
-keep class com.daricreative.catatanku.NoteEntity
-keep class com.daricreative.catatanku.NoteDao
-keep class com.daricreative.catatanku.AppDatabase

# Keep all Room classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Keep serialization
-keepattributes Signature
-keepattributes *Annotation*
