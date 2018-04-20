# QuasselDroid-ng

[![Release Version](https://img.shields.io/github/release/justjanne/QuasselDroid-ng/all.svg)](https://github.com/justjanne/QuasselDroid-ng/releases)

Quassel is a distributed, decentralized IRC client, written using C++ and Qt.
QuasselDroid is a pure-java client for the Quassel core, allowing you to connect
to your Quassel core using your Android ™ phone.

*Inspired by and based on [QuasselDroid](https://github.com/sandsmark/QuasselDroid)*

![Screenshot of QuasselDroid on Phone and Tablet](https://i.k8r.eu/4V7PhQ)

## Build Requirements

QuasselDroid requires you to have the latest version of gradle installed, and
a recent version of the Android SDK installed (and configured via the
environment variable ANDROID_HOME)

## Building


The build process uses gradle, `./gradlew assemble` builds all versions.

Unit tests are supported, and can be run with `./gradlew check`

To sign your releases, [generate a keypair](http://developer.android.com/tools/publishing/app-signing.html)
and create a file named `signing.properties` with the following content to let gradle automatically
sign your builds.

```
storeFile=/path/to/your/keystore/here.keystore
storePassword=passwordofyourkeystorehere
keyAlias=nameofyourkeyhere
keyPassword=passwordofyourkeyhere
```

## Authors

*(in chronological order of appearance)*

* **Frederik M. J. Vestre** (freqmod)
  Initial qdatastream deserialization attempts
* **Martin "Java Sucks" Sandsmark** (sandsmark)
  Legacy protocol implementation, (de)serializers, project (de)moralizer
* **Magnus Fjell** (magnuf)
  Legacy UI
* **Ken Børge Viktil** (Kenji)
  Legacy UI
* **Janne Koschinski** (justJanne)
  Rewrite, UI, Annotation Processors, Backend

## History

The project was originally started by freqmod in December 2010 as a simple PoC, and then expanded by
magnuf, sandsmark and Kenji until late 2014 into a usable Android app. At that time justJanne
started a first fork to introduce Holo design, then rebased that fork and turned it into a pull
request. Later finding more and more missing features, and more and more bugs, justJanne ended up
rewrite large parts of the code, and became the sole maintainer in late 2015.

This is a rewrite of that version, with focus on better performance, higher code quality, and
better UI.

The older implementation is still available at <https://github.com/sandsmark/QuasselDroid>.

## Used Libraries

### QuasselDroid

* [**Android Architecture Components: Lifecycle**](https://android.googlesource.com/platform/frameworks/support/+/master/lifecycle)
  Apache-2.0
* [**Android Architecture Components: Paging**](https://android.googlesource.com/platform/frameworks/support/+/master/paging)
  Apache-2.0
* [**Android Architecture Components: Persistence**](https://android.googlesource.com/platform/frameworks/support/+/master/persistence)
  Apache-2.0
* [**Android Architecture Components: Room**](https://android.googlesource.com/platform/frameworks/support/+/master/persistence)
  Apache-2.0
* [**Android Sliding Up Panel**](https://github.com/umano/AndroidSlidingUpPanel)
  Apache-2.0
* [**Android Support Library**](https://android.googlesource.com/platform/frameworks/support/+/master)
  Apache-2.0
* [**Android Support Library: Constraint Layout**](https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-3.0/constraintlayout)
  Apache-2.0
* [**atinject**](https://code.google.com/archive/p/atinject/)
  Apache-2.0
* [**AutoService**](https://github.com/google/auto/tree/master/service)
  Apache-2.0
* [**Better Link Movement Method**](https://github.com/Saketme/Better-Link-Movement-Method)
  Apache-2.0
* [**Butter Knife**](http://jakewharton.github.io/butterknife/)
  Apache-2.0
* [**Dagger 2**](https://google.github.io/dagger/)
  Apache-2.0
* [**Glide**](https://bumptech.github.io/glide/)
  Apache-2.0
* [**GlobTransformer**](https://bumptech.github.io/glide/)
  CC BY-SA 3.0
* [**Gson**](https://github.com/google/gson)
  Apache-2.0
* [**JavaPoet**](https://github.com/square/javapoet)
  Apache-2.0
* [**JetBrains Java Annotations**](https://github.com/JetBrains/java-annotations)
  Apache-2.0
* [**Kotlin Standard Library**](https://kotlinlang.org/)
  Apache-2.0
* [**LeakCanary**](https://github.com/square/leakcanary)
  Apache-2.0
* [**Material Design Icons: Community**](https://github.com/Templarian/MaterialDesign)
  SIL Open Font License v1.1
* [**Material Design Icons: Google**](https://github.com/google/material-design-icons)
  Apache-2.0
* [**Material Dialogs**](https://github.com/afollestad/material-dialogs)
  MIT
* [**MaterialProgressBar**](https://github.com/DreaminginCodeZH/MaterialProgressBar)
  Apache-2.0
* [**Quassel**](https://quassel-irc.org/)
  GPLv3
* [**Reactive Streams**](https://github.com/ReactiveX/RxJava)
  CC0
* [**RxJava**](https://github.com/ReactiveX/RxJava)
  Apache-2.0
* [**ThreeTen backport project**](http://www.threeten.org/threetenbp/)
  BSD 3-clause

## Themes
* [**Dracula**](https://draculatheme.com/)
  MIT
* [**Gruvbox**](https://github.com/morhetz/gruvbox)
  MIT
* [**Solarized**](http://ethanschoonover.com/solarized)
  MIT

## License

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
