# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

# Optimizations: If you don't want to optimize, use the
# proguard-android.txt configuration file instead of this one, which
# turns off the optimization flags.  Adding optimization introduces
# certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn
# off various optimizations known to have issues, but the list may not
# be complete or up to date. (The "arithmetic" optimization can be
# used if you are only targeting Android 2.0 or later.)  Make sure you
# test thoroughly if you go this route.
-optimizations !code/allocation/variable,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-dontobfuscate

# The remainder of this file is identical to the non-optimized version
# of the Proguard configuration file (except that the other file has
# flags to turn off optimization).

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep class * implements com.coremedia.iso.boxes.Box { *; }

# js-evaluator-for-android
-keepattributes JavascriptInterface
-keepclassmembers class * {
	@android.webkit.JavascriptInterface <methods>;
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** i(...);
}

# ADDED
-keep class com.google.zxing.client.android.camera.open.**
-keep class com.google.zxing.client.android.camera.exposure.**
-keep class com.google.zxing.client.android.common.executor.**

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.mjpeg.**


-dontwarn com.google.code.**
-dontwarn  org.apache.**
-dontwarn  jp.wasabeef.recyclerview.**
-dontwarn  com.nostra13.universalimageloader.**
-dontwarn  org.acra.**

#wasabeef recyclerview
-keep class jp.wasabeef.recyclerview.** { *; }
-keepattributes Signature
#HTTP Legacy
-keep class org.apache.** { *; }
-keepattributes Signature
#Universal Image Loader
-keep class com.nostra13.universalimageloader.** { *; }
-keepattributes Signature
#Acra
-keep class org.acra.**  { *; }
-keepattributes Signature
#Support libraries
-keep class com.android.** { *; }
-keepattributes Signature





# Keep the annotations
-keepattributes *Annotation*


-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''


-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * {
    public protected *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


    # Add this global rule
    -keepattributes Signature

    # This rule will properly ProGuard all the model classes in
    # the package com.yourcompany.models. Modify to fit the structure
    # of your app.
    -keepclassmembers class com.yourcompany.models.** {
      *;
    }



    # Okio
    -keep class sun.misc.Unsafe { *; }
    -dontwarn java.nio.file.*
    -dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
    -dontwarn okio.**


        -keep class com.google.android.gms.ads.** { *; }
        -dontwarn okio.**


        -keep public class * implements com.ixuea.android.downloader.db.DownloadDBController


        -keep public class * extends android.app.Activity
        -keepclasseswithmembers class * extends com.way4net.oner.lifa.plugin.ThemedFragment
        -keepclasseswithmembers class * extends com.way4net.oner.lifa.plugin.ThemedActivity
        -keep public class * extends android.app.Application
        -keep public class * extends android.app.Service
        -keep public class * extends android.content.BroadcastReceiver
        -keep public class * extends android.content.ContentProvider
        -keepattributes Signature #there were 1 classes trying to access generic signatures using reflection emfehlung von proguard selbst

        -keep public class * extends android.view.View {
              public <init>(android.content.Context);
              public <init>(android.content.Context, android.util.AttributeSet);
              public <init>(android.content.Context, android.util.AttributeSet, int);
              public void set*(...);
        }

        -keepclasseswithmembers class * {
             public <init>(android.content.Context, android.util.AttributeSet);
         }

        -keepclasseswithmembers class * {
            public <init>(android.content.Context, android.util.AttributeSet, int);
        }

        -keepclassmembers class * extends android.content.Context {
            public void *(android.view.View);
            public void *(android.view.MenuItem);
        }

        -keepclassmembers class * implements android.os.Parcelable {
            static ** CREATOR;
        }

        -keepclassmembers class **.R$* {
            public static <fields>;
        }

        -keepclassmembers class * {
            @android.webkit.JavascriptInterface <methods>;
        }

        -dontwarn okhttp3.**
        -dontwarn okio.**
        -dontwarn android.support.v4.**
        #-dontwarn javax.annotation.**
        #-dontwarn org.xmlpull.v1.**
        -dontnote android.net.http.*
        -dontnote org.apache.commons.codec.**
        -dontnote org.apache.http.**
        -dontnote okhttp3.**
        -dontnote org.kobjects.util.**
        -dontnote org.xmlpull.v1.**
        -keep class okhttp3.** {
              *;
         }

        -keep class org.xmlpull.v1.XmlSerializer {
            *;
        }
        -keep class org.xmlpull.v1.XmlPullParser{
            *;
        }
        -dontwarn org.xmlpull.v1.XmlPullParser

        -keep class org.xmlpull.v1.XmlSerializer {
            *;
        }
        -dontwarn org.xmlpull.v1.XmlSerializer

        -keep class org.kobjects.** { *; }
        -keep class org.ksoap2.** { *; }
        -keep class okio.** { *; }
        -keep class org.kxml2.** { *; }
        -keep class org.xmlpull.** { *; }

        #OKhttp RULES START
        -dontwarn okhttp3.**
        -dontwarn okio.**
        -dontwarn javax.annotation.**
        #OKhttp RULES END

        #picasso rules START
        -dontwarn com.squareup.okhttp.**
        #picasso rules END



        -keep class com.firebase.** { *; }
        -keep class org.apache.** { *; }
        -keepnames class com.shaded.fasterxml.** { *; }
        -keepnames class com.fasterxml.jackson.** { *; }
        -keepnames class javax.servlet.** { *; }
        -keepnames class org.ietf.jgss.** { *; }
        -dontwarn org.apache.**
        -dontwarn org.w3c.dom.**

        -keep class com.google.firebase.database.** { *; }
        -keep class com.google.firebase.** { *; }

        -dontwarn javax.xml.stream.**
        -keep public class * implements androidx.versionedparcelable.VersionedParcelable


# Keep custom model classes
-keep class com.google.firebase.example.fireeats.java.model.** { *; }
-keep class com.google.firebase.example.fireeats.kotlin.model.** { *; }

# https://github.com/firebase/FirebaseUI-Android/issues/1175
-dontwarn okio.**
-dontwarn retrofit2.Call
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-keep class android.support.v7.widget.RecyclerView { *; }
-keep class androidx.recyclerview.widget.RecyclerView { *; }
