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

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep gson
-keep public class com.google.gson.** { *; }

# For RxJava
-dontwarn sun.misc.Unsafe
-dontwarn sun.misc.**
-keep class rx.internal.util.unsafe.** { *; }

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}


# For retrolambda
-dontwarn java.lang.invoke.*

# Keep Retrofit 2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Remove logging
-assumenosideeffects class io.reark.reark.utils.Log { *; }
-assumenosideeffects class android.util.Log { *; }
