#!/bin/sh

echo sdk.dir=$ANDROID_HOME > AndroidSlidingUpPanel/local.properties
cd AndroidSlidingUpPanel
gradle build
cd ..
cp AndroidSlidingUpPanel/library/build/outputs/aar/library-release.aar app/libs/library-release.aar
rm AndroidSlidingUpPanel/local.properties

echo sdk.dir=$ANDROID_HOME > aspm/local.properties
cd aspm
gradle build jar shadowJar
cd ..
cp aspm/annotations/build/libs/annotations.jar app/libs/annotations.jar
cp aspm/compiler/build/libs/compiler-all.jar app/libs/compiler-all.jar
rm aspm/local.properties

echo sdk.dir=$ANDROID_HOME > local.properties
gradle assembleRelease -x lintVitalRelease
rm local.properties
rm app/libs/*