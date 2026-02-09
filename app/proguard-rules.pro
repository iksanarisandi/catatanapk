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

# Kotlin
-keep class kotlin.reflect.** { *; }
-keep interface kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**
-keep class kotlin.Metadata { *; }

# Kotlin coroutines (additional)
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers,allowobfuscation class * {
    @kotlinx.coroutines.internal.MainDispatcherFactory <methods>;
}
-keepclassmembernames class * {
    native <methods>;
}

# AndroidX
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
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
