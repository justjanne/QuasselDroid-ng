#!/bin/sh
cp local.properties aspm/local.properties
cd AndroidSlidingUpPanel
gradle build
cd ..
rm aspm/local.properties
cp AndroidSlidingUpPanel/library/build/outputs/aar/library-release.aar app/libs/

cp local.properties aspm/local.properties
cd aspm
gradle build jar shadowJar
cd ..
rm aspm/local.properties
cp aspm/annotations/build/libs/annotations.jar app/libs/
cp aspm/compiler/build/libs/compiler-all.jar app/libs/

gradle assembleRelease -x lintVitalRelease

rm app/libs/*