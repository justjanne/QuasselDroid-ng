Build Guide
===========

First build libraries:

```
cd AndroidSlidingUpPanel
gradle build
```

```
cd aspm
gradle shadowJar
```

Then copy or link the files at the appropriate places:

```
cp aspm/annotations/build/libs/annotations.jar app/libs/
cp aspm/compiler/build/libs/compiler-all.jar app/libs/
cp AndroidSlidingUpPanel/library/build/outputs/aar/library-release.aar app/libs/
```

Then invoke gradle for the main project:

```
gradle assembleRelease
```
