# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android\android-sdk-windows/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dontshrink
-applymapping hsmapping/mapping_v7.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#保护android组件名字不混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
#保护js调用方法不被混淆
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
######下面是自己的一些特殊的配置####################################################
-keep class com.game.sdk.domain.**{*;} #保护javaBean
# keep annotated by NotProguard 保护使用NotProguard不混淆
# 特别注意内部类会被混淆掉
-keep class com.game.sdk.domain.NotProguard
-keep @com.game.sdk.domain.NotProguard class * {*;}

-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <methods>;
}
#
-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <fields>;
}
#
-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <init>(...);
}

#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================okvolly_1.1.0.jar==========================
-dontwarn com.kymjs.rxvolley.**
-keep class com.kymjs.rxvolley.** {*;}

#==================alipaySdk支付宝支付==========================
-dontwarn com.alipay.**
-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-dontwarn org.json.alipay.**
-dontwarn com.alipay.android.app.IAlixPay.**
-keep class com.alipay.android.app.IAlixPay.**
-keep class com.alipay.** {*;}
-keep class com.ta.utdid2.** {*;}
-keep class com.ut.device.** {*;}
-keep class org.json.alipay.** {*;}

#apache
-dontwarn org.apache.**
-keep class org.apache.** {*;}
-dontwarn android.net.http.**
-keep class android.net.http.** { *;}
-keep class com.android.volley.**{*;}


