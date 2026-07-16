# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Strip debug log calls in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Timber
-keep class timber.log.* { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.ktx.dormitory.data.** { *; }

# Room
-keep class androidx.room.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
