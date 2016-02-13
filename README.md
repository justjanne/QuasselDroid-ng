# QuasselDroid-ng

[![Build Status](https://img.shields.io/jenkins/s/https/ci.kuschku.de/QuasselDroid-ng.svg)](https://ci.kuschku.de/job/QuasselDroid-ng/)
[![Release Version](http://github-release-version.herokuapp.com/github/justjanne/QuasselDroid-ng/release.svg?style=flat)](https://github.com/justjanne/QuasselDroid-ng/releases)

Quassel is a distributed, decentralized IRC client, written using C++ and Qt.
QuasselDroid is a pure-java client for the Quassel core, allowing you to connect
to your Quassel core using your Android ™ phone.

*Inspired by and based on [QuasselDroid](https://github.com/sandsmark/QuasselDroid)*

![Screenshot of QuasselDroid on Phone and Tablet](https://projects.kuschku.de/quasseldroid/assets/devices.png)

##Build Requirements

QuasselDroid requires you to have the latest version of gradle installed, and 
a recent version of the Android SDK installed (and configured via the
environment variable ANDROID_HOME)

QuasselDroid uses the following libraries (although these are automatically
downloaded from maven central):

* [**AndroidSlidingUpPanel**](https://github.com/justjanne/AndroidSlidingUpPanel)
  Apache 2.0
* [**libquassel-java**](https://github.com/justjanne/libquassel-java)
  LGPL 3.0
* [**Guava**](https://github.com/google/guava)
  Apache 2.0
* [**Joda-Time**](https://github.com/JodaOrg/joda-time/)
  Apache 2.0
* [**Joda-Convert**](https://github.com/JodaOrg/joda-convert)
  Apache 2.0
* [**Material Drawer**](https://github.com/mikepenz/MaterialDrawer)
  Apache 2.0
* [**Material Dialogs**](https://github.com/google/guava)
  MIT
* [**ButterKnife**](https://github.com/JakeWharton/butterknife/)
  Apache 2.0
* [**Android Support Libraries**](http://developer.android.com/tools/support-library/index.html)
  Apache 2.0  
  *Android Support Library requires the corresponding package to be installed in
  the SDK manager*

##Building

The build process uses gradle, and requires it to be available as command
"gradle". Use the `install.sh` script to pull and build the dependencies.

Run `gradle tasks` to see possible tasks, `gradle assembleRelease` to assemble a
release build and `gradle installDebug` to install a debug build on a device
connected via `adb`.

To sign your releases, [generate a keypair](http://developer.android.com/tools/publishing/app-signing.html)
and create a file named `signing.gradle` in the `app/` folder with the following
content to let gradle automatically sign your builds.

```
    storeFile=/path/to/your/keystore/here.keystore
    storePassword=passwordofyourkeystorehere
    keyAlias=nameofyourkeyhere
    keyPassword=passwordofyourkeyhere
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

##History

The project was originally started by freqmod in December 2010 as a simple PoC, and then expanded by magnuf, sandsmark and Kenji until late 2014 into a usable Android app. At that time justJanne started a first fork to introduce Holo design, then rebased that fork and turned it into a pull request. Later finding more and more missing features, and more and more bugs, justJanne ended up rewrite large parts of the code, and became the sole maintainer in late 2015.

As it became obvious that it would be increasingly harder to properly maintain the software, justJanne decided to rewrite it around that time, and implement the full protocol (not just a subset) this time around, also focusing on better design this time.

The older implementation is still available at <https://github.com/sandsmark/QuasselDroid>.

##License

> This program is free software: you can redistribute it and/or modify it
> under the terms of the GNU General Public License as published by the Free
> Software Foundation, either version 3 of the License, or (at your option)
> any later version.

> This program is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> GNU General Public License for more details.

> You should have received a copy of the GNU General Public License along
> with this program.  If not, see &lt;<http://www.gnu.org/licenses/>&gt;.
