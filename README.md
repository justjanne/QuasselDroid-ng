# QuasselDroid-ng

Quassel is a distributed, decentralized IRC client, written using C++ and Qt.
QuasselDroid is a pure-java client for the Quassel core, allowing you to connect
to your Quassel core using your Android ™ phone.

*Inspired by and based on [QuasselDroid](https://github.com/sandsmark/QuasselDroid)*

##Build Requirements

QuasselDroid requires you to have the latest version of gradle installed, and 
a recent version of the Android SDK installed (and configures via sdk.dir in
local.properties)

Additionally, it requires you to build (in the same way) first the following
libraries:

* https://github.com/justjanne/AndroidSlidingUpPanel
* https://github.com/justjanne/aspm

The libraries of each have to be placed in `app/libs/` for gradle to find them.

QuasselDroid uses the following libraries (although these are automatically
downloaded from maven central):

* [**libquassel-java**](https://github.com/justjanne/libquassel-java) LGPL 3.0
* [**Guava**](https://github.com/google/guava) Apache 2.0
* [**Joda-Time**](https://github.com/JodaOrg/joda-time/) Apache 2.0
* [**Joda-Convert**](https://github.com/JodaOrg/joda-convert) Apache 2.0
* [**Material Drawer**](https://github.com/mikepenz/MaterialDrawer) Apache 2.0
* [**Material Dialogs**](https://github.com/google/guava) MIT
* [**ButterKnife**](https://github.com/JakeWharton/butterknife/) Apache 2.0
* [**Android Support Libraries**](http://developer.android.com/tools/support-library/index.html) Apache 2.0  
*Android Support Library requires the corresponding package to be installed in the SDK manager*

##Building

The build process uses gradle. Run `gradle tasks` to see possible tasks,
`gradle assembleRelease` to assemble a release build and `gradle installDebug`
to install a debug build on a device connected via `adb`.

To sign your releases, [generate a keypair]() and create a file named
`signing.gradle` in the `app/` folder with the following content to let gradle
automatically sign your builds

```groovy
android {
    signingConfigs {
        release {
            storeFile file("/path/to/your/keystore/here.keystore")
            storePassword "passwordofyourkeystorehere"
            keyAlias "nameofyourkeyhere"
            keyPassword "passwordofyourkeyhere"
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

##Note

This is a very early alpha version, and should not be used as daily driver
just yet.

##Authors

*(in chronological order of appearance)*

* **Frederik M. J. Vestre** (freqmod)  
  Initial qdatastream deserialization attempts
* **Martin "Java Sucks" Sandsmark** (sandsmark)  
  Previous protocol implementation, previous (de)serializers, project (de)moralizer
* **Magnus Fjell** (magnuf)  
  Legacy UI, Previous Android stuff
* **Ken Børge Viktil** (Kenji)  
  Legacy UI, Previous Android stuff
* **Janne Koschinski** (justJanne)  
  New deserialization, Current UI version, Annotation Processors, Push Notification frontend
* **Pierre-Hugues Husson** (phhusson)  
  Push Notification backend