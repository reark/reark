# Add project specific ProGuard rules here.

-verbose
-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Optimization step doesn't update things correctly
-optimizations !code/allocation/variable

-dontwarn okio.**
-dontwarn org.apache.http.**
-dontwarn com.squareup.okhttp.internal.huc.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.client.ApacheClient$GenericEntityHttpRequest
-dontwarn retrofit.client.ApacheClient$GenericHttpRequest
-dontwarn retrofit.client.ApacheClient$TypedOutputEntity

# For RxJava
-dontwarn sun.misc.Unsafe

# For retrolambda
-dontwarn java.lang.invoke.*

# Keep Retrofit
-keep class retrofit.** { *; }
-keepclasseswithmembers class * { @retrofit.** *; }
-keepclassmembers class * { @retrofit.** *; }

# Remove logging
-assumenosideeffects class io.reark.reark.utils.Log { *; }
-assumenosideeffects class android.util.Log { *; }
