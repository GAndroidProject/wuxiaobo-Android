# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.AppCompatActivity      # 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-dontwarn android.support.v4.**
-keep public class * extends android.support.v4.**

-dontoptimize
-dontpreverify


-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-dontwarn android.net.**  #忽略某个包的警告
-keep class android.net.SSLCertificateSocketFactory{*;}
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

#↓↓↓↓↓↓↓实体类↓↓↓↓↓↓↓
-keep public class com.xiaoe.common.entitys.**{*;}
# 版本更新模块
-keep public class com.xiaoe.shop.wxb.business.upgrade.**{*;}
# 极光推送
-keep public class com.xiaoe.shop.wxb.common.jpush.**{*;}
#↑↑↑↑↑↑↑实体类↑↑↑↑↑↑↑

# webView处理，项目中没有使用到webView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**
-keep public class javax.**
-keep public class android.webkit.**

-keepclasseswithmembers class * {
    ... *JNI*(...);
}

-keepclasseswithmembernames class * {
	... *JRI*(...);
}

-keep class **JNI* {*;}

#↓↓↓↓↓↓↓fastjson↓↓↓↓↓↓↓
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }
#↑↑↑↑↑↑↑fastjson↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓极光↓↓↓↓↓↓↓
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

# 2.0.5 ~ 2.1.7 版本有引入 gson 和 protobuf，增加排除混淆的配置。（2.1.8 版本不需配置）
#==================gson && protobuf==========================
-dontwarn com.google.**
#-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}
#↑↑↑↑↑↑↑极光↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓bugly↓↓↓↓↓↓↓↓
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#↑↑↑↑↑↑↑bugly↑↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓↓友盟↓↓↓↓↓↓↓↓
-dontwarn com.umeng.**
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.xiaoe.shop.wxb.R$*{
    public static final int *;
}
#↑↑↑↑↑↑↑↑友盟↑↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓↓okhttp3↓↓↓↓↓↓↓↓
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
-dontwarn okio.**
-keep class okio.**{*;}
#↑↑↑↑↑↑↑↑okhttp3↑↑↑↑↑↑↑↑


#↓↓↓↓↓↓↓↓gson↓↓↓↓↓↓↓↓
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}
#这句非常重要，主要是滤掉 com.bgb.scan.model包下的所有.class文件不进行混淆编译
-keep class com.bgb.scan.model.** {*;}
#↑↑↑↑↑↑↑↑gson↑↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓↓facebook↓↓↓↓↓↓↓↓
-keep class com.facebook.** {*;}
-dontwarn com.facebook.**
#↑↑↑↑↑↑↑↑facebook↑↑↑↑↑↑↑↑

# Fresco
-keep class com.facebook.fresco.** {*;}
-keep interface com.facebook.fresco.** {*;}
-keep enum com.facebook.fresco.** {*;}

#↓↓↓↓↓↓↓↓glide↓↓↓↓↓↓↓↓
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#↑↑↑↑↑↑↑↑glide↑↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓↓微信sdk ↓↓↓↓↓↓↓↓
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
#↑↑↑↑↑↑↑↑微信sdk↑↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓↓eventbus↓↓↓↓↓↓↓↓
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#↑↑↑↑↑↑↑↑eventbus↑↑↑↑↑↑↑↑


#↓↓↓↓↓↓↓↓org.apache.http.legacy↓↓↓↓↓↓↓↓
-dontwarn android.net.**
-keep class android.net.** { *;}
-dontwarn com.android.internal.http.multipart.**
-keep class com.android.internal.http.multipart.** {*;}
-dontwarn org.apache.**
-keep class org.apache.**{*;}
#↑↑↑↑↑↑↑↑org.apache.http.legacy↑↑↑↑↑↑↑↑


#↓↓↓↓↓↓↓↓com.pnikosis.materialishprogress↓↓↓↓↓↓↓↓
-dontwarn com.pnikosis.materialishprogress.**
-keep class com.pnikosis.materialishprogress.** {*;}
#↑↑↑↑↑↑↑↑com.pnikosis.materialishprogress↑↑↑↑↑↑↑↑

#↓↓↓↓↓↓↓↓butterknife↓↓↓↓↓↓↓↓
-keep public class * implements butterknife.Unbinder {
    public <init>(**, android.view.View);
}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#↑↑↑↑↑↑↑↑butterknife↑↑↑↑↑↑↑↑


#↓↓↓↓↓↓↓↓腾讯tbs(x5内核)↓↓↓↓↓↓↓↓
-keep class com.tencent.smtt.export.external.**{
    *;
}

-keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
	*;
}

-keep class com.tencent.smtt.sdk.CacheManager {
	public *;
}

-keep class com.tencent.smtt.sdk.CookieManager {
	public *;
}

-keep class com.tencent.smtt.sdk.WebHistoryItem {
	public *;
}

-keep class com.tencent.smtt.sdk.WebViewDatabase {
	public *;
}

-keep class com.tencent.smtt.sdk.WebBackForwardList {
	public *;
}

-keep public class com.tencent.smtt.sdk.WebView {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
	public static final <fields>;
	public java.lang.String getExtra();
	public int getType();
}

-keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebView$PictureListener {
	public <fields>;
	public <methods>;
}


-keepattributes InnerClasses

-keep public enum com.tencent.smtt.sdk.WebSettings$** {
    *;
}

-keep public enum com.tencent.smtt.sdk.QbSdk$** {
    *;
}

-keep public class com.tencent.smtt.sdk.WebSettings {
    public *;
}


-keepattributes Signature
-keep public class com.tencent.smtt.sdk.ValueCallback {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebViewClient {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.DownloadListener {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebChromeClient {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
	public <fields>;
	public <methods>;
}

-keep class com.tencent.smtt.sdk.SystemWebChromeClient{
	public *;
}
# 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
	public protected *;
}

# 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebIconDatabase {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebStorage {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.DownloadListener {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.QbSdk {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
	public <fields>;
	public <methods>;
}
-keep public class com.tencent.smtt.sdk.CookieSyncManager {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.Tbs* {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.LogFileUtils {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.TbsLog {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.TbsLogClient {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.CookieSyncManager {
	public <fields>;
	public <methods>;
}

# Added for game demos
-keep public class com.tencent.smtt.sdk.TBSGamePlayer {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.Apn {
	public <fields>;
	public <methods>;
}
-keep class com.tencent.smtt.** {
	*;
}
# end


-keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
	public <fields>;
	public <methods>;
}

-keep class MTT.ThirdAppInfoNew {
	*;
}

-keep class com.tencent.mtt.MttTraceEvent {
	*;
}

# Game related
-keep public class com.tencent.smtt.gamesdk.* {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.TBSGameBooter {
        public <fields>;
        public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
	public protected *;
}

-keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
	public *;
}
#↑↑↑↑↑↑↑↑腾讯tbs(x5内核)↑↑↑↑↑↑↑↑


#↓↓↓↓↓↓↓↓BaseRecyclerViewAdapterHelper↓↓↓↓↓↓↓↓
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
           <init>(android.view.View);
}
#↑↑↑↑↑↑↑↑BaseRecyclerViewAdapterHelper↑↑↑↑↑↑↑↑

# kotlin防止混淆
-keepclassmembers data class com.xiaoe.common.entitys.** { *; }
