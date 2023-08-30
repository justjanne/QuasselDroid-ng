# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/lib/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# The project is GPL anyway, obfuscation is useless.
-dontobfuscate

# Keep our invokers
-keep class * implements de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invoker {
    static ** INSTANCE;
}
-keep class * implements de.kuschku.libquassel.quassel.syncables.interfaces.invokers.InvokerRegistry {
    static ** INSTANCE;
}

# remove unnecessary warnings
# Android HTTP Libs
-dontnote android.net.http.**
-dontnote org.apache.http.**
# Kotlin stuff
-dontnote kotlin.**
# Dagger
-dontwarn com.google.errorprone.annotations.*
# Retrofit
-dontwarn retrofit2.**
# Annotation used by Retrofit on Java 8 VMs
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn javax.annotation.concurrent.GuardedBy
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
# Okio
-dontwarn okio.**
-dontwarn org.conscrypt.**
# OkHttp3
-dontwarn okhttp3.**
