# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

-dontobfuscate

-dontwarn javax.annotation.**
-dontwarn javax.lang.model.**
-dontwarn javax.tools.**
-dontwarn com.google.j2objc.annotations.**
-dontwarn java.lang.ClassValue
-dontwarn sun.misc.Unsafe
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.processing.ProcessingEnvironment
-dontwarn com.nineoldandroids.view.animation.AnimatorProxy

-keepclasseswithmembernames class de.kuschku.** {
    <methods>;
    <fields>;
}

-keepclassmembers class de.kuschku.** {
    <methods>;
    <fields>;
}


#########################################
## Bufferknife                         ##
#########################################
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


#########################################
## EventBus                            ##
#########################################
-keepclassmembers class ** {
    public void onEvent*(***);
}

-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#########################################
## RetroLambda                         ##
#########################################
-dontwarn java.lang.invoke.*