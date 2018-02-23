# QuasselDroid-ng

[![Release Version](http://github-release-version.herokuapp.com/github/justjanne/QuasselDroid-ng/release.svg?style=flat)](https://github.com/justjanne/QuasselDroid-ng/releases)

Quassel is a distributed, decentralized IRC client, written using C++ and Qt.
QuasselDroid is a pure-java client for the Quassel core, allowing you to connect
to your Quassel core using your Android ™ phone.

*Inspired by and based on [QuasselDroid](https://github.com/sandsmark/QuasselDroid)*

![Screenshot of QuasselDroid on Phone and Tablet](https://i.imgur.com/XUqcN7j.png)

## Warning

This is a very early alpha version, and should not be used in production.

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

* [**Android Support**](http://developer.android.com/tools/support-library/index.html)
  Apache 2.0
* [**Android App Architecture**](https://developer.android.com/topic/libraries/architecture/guide.html)
  Apache 2.0
* [**ThreeTen Backport**](http://www.threeten.org/threetenbp/)
  BSD 3-clause
* [**ButterKnife**](https://github.com/JakeWharton/butterknife/)
  Apache 2.0
* [**ACRA**](https://github.com/JakeWharton/butterknife/)
  Apache 2.0

### InvokerGenerator, a preprocessor included in this project

* [**Google AutoService**](https://github.com/google/auto/tree/master/service)
  Apache 2.0
* [**Square JavaPoet**](https://github.com/square/javapoet)
  Apache 2.0

## License

> This program is free software: you can redistribute it and/or modify it
> under the terms of the GNU General Public License as published by the Free
> Software Foundation, either version 2 of the License, or (at your option)
> any later version.

> This program is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> GNU General Public License for more details.

> You should have received a copy of the GNU General Public License along
> with this program.  If not, see &lt;<http://www.gnu.org/licenses/>&gt;.
