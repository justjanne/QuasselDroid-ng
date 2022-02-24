# [Quasseldroid]

[![Release Version](https://img.shields.io/github/release/justjanne/quasseldroid-ng/all.svg)](https://github.com/justjanne/Quasseldroid-ng/releases)

Quassel is a distributed, decentralized IRC client, written using C++ and Qt. Quasseldroid is a
pure-java client for the Quassel core, allowing you to connect to your Quassel core using your
Android™ phone.

[![Screenshot of Quasseldroid on Phone and Tablet](https://i.k8r.eu/2G2ToAh.png)](https://i.k8r.eu/2G2ToA.png)

## Build Requirements

Quasseldroid requires you to have the latest version of gradle installed, and a recent version of
the Android SDK installed (and configured via the environment variable ANDROID_HOME)

## Building

The build process uses gradle, `./gradlew assemble` builds all versions.

Unit tests are supported, and can be run with `./gradlew check`

To sign your releases, [generate a keypair] and create a file named `signing.properties` with the
following content to let gradle automatically sign your builds.

```
storeFile=/path/to/your/keystore/here.keystore
storePassword=passwordofyourkeystorehere
keyAlias=nameofyourkeyhere
keyPassword=passwordofyourkeyhere
```

## Authors

* **Janne Mareike Koschinski** (justJanne)
  Rewrite, UI, Annotation Processors, Backend

## Acknowledgements

This project was inspired by and is based on [Quasseldroid Legacy] as well as [Quassel].

Authors of legacy Quasseldroid:

* **Frederik M. J. Vestre** (freqmod)
  Initial qdatastream deserialization attempts
* **Martin "Java Sucks" Sandsmark** (sandsmark)
  Legacy protocol implementation, (de)serializers, project (de)moralizer
* **Magnus Fjell** (magnuf)
  Legacy UI
* **Ken Børge Viktil** (Kenji)
  Legacy UI

## License

> This program is free software: you can redistribute it and/or modify it
> under the terms of the GNU General Public License version 3 as published
> by the Free Software Foundation.

> This program is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> GNU General Public License for more details.

> You should have received a copy of the GNU General Public License along
> with this program.  If not, see &lt;<http://www.gnu.org/licenses/>&gt;.

[Quasseldroid]: https://quasseldroid.info/
[generate a keypair]: http://developer.android.com/tools/publishing/app-signing.html
[Quasseldroid Legacy]: https://github.com/sandsmark/quasseldroid
[Quassel]: https://quassel-irc.org/
