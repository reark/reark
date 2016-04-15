# Add project specific ProGuard rules here.

-verbose
-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Optimization step doesn't update things correctly
-optimizations !code/allocation/variable




# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# For RxJava
-dontwarn sun.misc.Unsafe

# For retrolambda
-dontwarn java.lang.invoke.*

# Keep Retrofit 2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions


# Remove logging
-assumenosideeffects class io.reark.reark.utils.Log { *; }
-assumenosideeffects class android.util.Log { *; }
